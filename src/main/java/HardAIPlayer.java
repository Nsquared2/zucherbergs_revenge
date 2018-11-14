import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Basic AI player class that corresponds to the Hard difficulty.
 * This player has deterministic behavior and needs to be trained by the AI handler.
 * Trains using multi layer perceptron base policy and naive bayes online learning
 */

public class HardAIPlayer extends AIPlayer{
    Random rand = new Random();
    float comm_thresh = 0.9f;

    HardAIPlayer(int id, String name){
        super(id, name, new ArrayList<>());
    }

    /**
     * Returns a new Communication that will be sent to a random player, and has a random communication type.
     */
    Communication message_action(ArrayList<Integer> ids){
        if(rand.nextFloat() > this.comm_thresh){
            int num_coms = CommType.values().length;
            CommType comm_type = CommType.values()[rand.nextInt(num_coms)];
            int receiver = ids.get(rand.nextInt(ids.size()));

            Communication comm = new Communication(comm_type, this.id, receiver);
            return comm;
        }
        else{
            return null;
        }
    }

    /**
     * Creates a random Action for each of the Player IDs given as input.
     */
    ArrayList<Action> round_action(ArrayList<Integer> ids) {
        int num_actions = ActionType.values().length;
        ArrayList<Action> actions = new ArrayList<Action>(0);

        for(int reciever: ids) {
            ActionType action_type = ActionType.values()[rand.nextInt(num_actions)];

            Action action = new Action(action_type, this.id, reciever);
            actions.add(action);
        }

        return actions;
    }

    @Override
    Communication message_action() {
        return null;
    }

    @Override
    ArrayList<Action> round_action() {
        return null;
    }

    @Override
    void update_policy(ArrayList<HashMap<Integer, ActionType>> round_results) {

    }
}
