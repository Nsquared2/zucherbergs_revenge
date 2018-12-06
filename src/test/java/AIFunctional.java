import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AIFunctional {
    /**
     * AI players should learn strategies of other players
     */
    @Test
    public void AI2() {
        ActionType dummy_choice = ActionType.BETRAY;
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(1));
        ActionType correct_action = ai.maximizeValue(dummy_choice);
        ActionType ai_action = ActionType.BETRAY;

        for (int r = 0; r < 2; r++) {
            HashMap<Integer, Action> ai_actions = ai.round_action();
            ActionType action = ai_actions.get(1).getAction();

            HashMap<Integer, ActionType> dummy_results = new HashMap<>();
            dummy_results.put(1, dummy_choice);
            ai.update_policy(dummy_results);

            ai_action = action;
        }

        Assert.assertEquals(correct_action, ai_action);
    }

    /**
     * Each AI player should send the correct number of round decisions to server at the end of each round
     */
    @Test
    public void AI3(){
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(2));
        HashMap<Integer, Action> ai_actions = ai.round_action();
        Assert.assertEquals(2, ai_actions.size());
    }

    /**
     * AI difficulty rankings should be observable
     */
    @Test
    public void AI4(){
        ActionType dummy_choice = ActionType.BETRAY;
        AIPlayer ai_med = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(1));
        AIPlayer ai_hard = AIHandler.createAi("hard", 1, "bob", AITestUtil.dummy_ids(1));

        //Ordered list of actions dummy player will do
        ArrayList<ActionType> dummy_actions = new ArrayList<>();
        dummy_actions.add(ActionType.BETRAY);
        dummy_actions.add(ActionType.COOPERATE);
        dummy_actions.add(ActionType.IGNORE);
        ArrayList<ActionType> action_ans = new ArrayList<>();
        for(ActionType a: dummy_actions){
            action_ans.add(ai_med.maximizeValue(a));
        }

        //Ordered list of communication sequences that trigger actions above
        ArrayList<ArrayList<Communication>> comm_sets_med = genCommSets(1);
        ArrayList<ArrayList<Communication>> comm_sets_hard = genCommSets(1);

        //Indexes that determine what dummy player will do for this game
        int[] action_ids = {0,2,1,0,0,2,2,1,2,1};

        //Game session/Training
        for (int r = 0; r < 2; r++) {
            //Determine comms dummy player will send to ai players. Sends same set of comms to both players
            ArrayList<Communication> med_comms = comm_sets_med.get(action_ids[r]);
            ArrayList<Communication> hard_comms = comm_sets_hard.get(action_ids[r]);

            //Send comms to ai players
            for(int c=0; c < med_comms.size(); c++) {
                ai_med.receiveMessage(med_comms.get(c));
                ai_hard.receiveMessage(hard_comms.get(c));
            }

            //Get actions for both ai players
            HashMap<Integer, Action> med_actions = ai_med.round_action();
            ActionType med_action = med_actions.get(1).getAction();
            HashMap<Integer, Action> hard_actions = ai_hard.round_action();
            ActionType hard_action = hard_actions.get(0).getAction();

            //update AI players
            HashMap<Integer, ActionType> dummy_results = new HashMap<>();
            dummy_results.put(1, dummy_actions.get(action_ids[r]));
            dummy_results.put(0, dummy_actions.get(action_ids[r]));
            ai_med.update_policy(dummy_results);
            ai_hard.update_policy(dummy_results);
        }

        //Evaluate classifiers on each action possibility after training
        ArrayList<ActionType> med_actions = new ArrayList<>();
        ArrayList<ActionType> hard_actions = new ArrayList<>();
        for(int r=0; r < 3; r++){
            //Determine comms dummy player will send to ai players. Sends same set of comms to both players
            ArrayList<Communication> med_comms = comm_sets_med.get(action_ids[r]);
            ArrayList<Communication> hard_comms = comm_sets_hard.get(action_ids[r]);

            //Send comms to ai players
            for(int c=0; c < med_comms.size(); c++) {
                ai_med.receiveMessage(med_comms.get(c));
                ai_hard.receiveMessage(hard_comms.get(c));
            }


            //update AI players
            HashMap<Integer, ActionType> dummy_results = new HashMap<>();
            dummy_results.put(1, dummy_actions.get(action_ids[r]));
            dummy_results.put(0, dummy_actions.get(action_ids[r]));
            ai_med.update_policy(dummy_results);
            ai_hard.update_policy(dummy_results);

            // Get actions for both ai players
            HashMap<Integer,Action> med_action_array = ai_hard.round_action();
            ActionType med_action = med_action_array.get(0).getAction();
            med_actions.add(med_action);
            HashMap<Integer, Action> hard_action_array = ai_hard.round_action();
            ActionType hard_action = hard_action_array.get(0).getAction();
            hard_actions.add(hard_action);
        }

        float med_accuracy = getAccuracy(action_ans, med_actions);
        float hard_accuracy = getAccuracy(action_ans, hard_actions);
        Assert.assertTrue((hard_accuracy >= med_accuracy));
    }

    /**
     * Game data should be saved externally in between games
     */
    @Test
    public void AI6(){
        File folder = new File("./ai_test/dummy_save/");
        deleteFiles(folder);
        AIHandler.setDataPathForTesting("./ai_test/dummy_save/");
        ActionType dummy_choice = ActionType.BETRAY;
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(1));
        ActionType ai_action = ActionType.BETRAY;


        for (int r = 0; r < 2; r++) {
            HashMap<Integer, Action> ai_actions = ai.round_action();
            ActionType action = ai_actions.get(1).getAction();

            HashMap<Integer, ActionType> dummy_results = new HashMap<>();
            dummy_results.put(1, dummy_choice);
            ai.update_policy(dummy_results);
        }

        ArrayList<AIPlayer> ais = new ArrayList<>();
        ais.add(ai);
        AIHandler.saveAiPlayerObservations(ais);


        File data_dir = new File(AIHandler.data_path);
        File[] files = data_dir.listFiles();
        HashSet<String> names = new HashSet<String>();
        for(File file: files){names.add(file.getName());}

        HashSet<String> ans = new HashSet<>();
        ans.add("game1.arff");

        Assert.assertEquals(ans, names);

    }

    /**
     * Training AI’s should change starting policies
     */
    @Test
    public void AI7(){
        AIHandler.setDataPathForTesting("nonexist");
        TrainableAIPlayer pre_med_ai = (TrainableAIPlayer) AIHandler.createAi("medium", 0, "med", AITestUtil.dummy_ids(1));
        TrainableAIPlayer pre_hard_ai = (TrainableAIPlayer) AIHandler.createAi("hard", 0, "hard", AITestUtil.dummy_ids(1));

        AITestUtil.setAIDirs();
        AIHandler.trainBasePolicies();

        TrainableAIPlayer post_med_ai = (TrainableAIPlayer) AIHandler.createAi("medium", 0, "med_post", AITestUtil.dummy_ids(1));
        TrainableAIPlayer post_hard_ai = (TrainableAIPlayer) AIHandler.createAi("hard", 0, "hard_post", AITestUtil.dummy_ids(1));
        Assert.assertNotEquals(pre_med_ai.getBase_model(), post_med_ai.getBase_model());
        Assert.assertNotEquals(pre_hard_ai.getBase_model(), post_hard_ai.getBase_model());
    }

    /**
     * Easy AI Player should function in a game process
     */
    @Test
    public void AI8(){
        ActionType dummy_choice = ActionType.BETRAY;
        AIPlayer ai = AIHandler.createAi("easy", 0, "joe", AITestUtil.dummy_ids(1));

        for (int r = 0; r < 2; r++) {
            HashMap<Integer, Action> ai_actions = ai.round_action();
            ActionType action = ai_actions.get(1).getAction();

            HashMap<Integer, ActionType> dummy_results = new HashMap<>();
            dummy_results.put(1, dummy_choice);
            ai.update_policy(dummy_results);
        }

    }

    private ArrayList<ArrayList<Communication>> genCommSets(int ai_id){
        //Ordered list of communication sequences that trigger actions above
        ArrayList<ArrayList<Communication>> comm_sets = new ArrayList<>();
        ArrayList<Communication> comms_1 = new ArrayList<>();
        comms_1.add(new Communication(CommType.REQUEST_COOP, 1, ai_id));
        comms_1.add(new Communication(CommType.PROMISE_COOP, 1, ai_id));
        comm_sets.add(comms_1);
        ArrayList<Communication> comms_2 = new ArrayList<>();
        comms_2.add(new Communication(CommType.REQUEST_COOP, 1, ai_id));
        comms_2.add(new Communication(CommType.PROMISE_BETRAY, 1, ai_id));
        comm_sets.add(comms_2);
        ArrayList<Communication> comms_3 = new ArrayList<>();
        comms_3.add(new Communication(CommType.REQUEST_IGNORE, 1, ai_id));
        comms_3.add(new Communication(CommType.PROMISE_IGNORE, 1, ai_id));
        comm_sets.add(comms_3);

        return comm_sets;
    }

    private void deleteFiles(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFiles(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    private float getAccuracy(ArrayList<ActionType> truth, ArrayList<ActionType> actual){
        float num_right = 0;
        for(int i=0; i < truth.size(); i++){
            if(truth.get(i).equals(actual.get(i)))
                num_right += 1;
        }
        return num_right/(float)truth.size();
    }
}
