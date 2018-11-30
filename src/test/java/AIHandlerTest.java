import org.junit.Test;
import org.junit.Assert;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.File;
import java.util.*;

public class AIHandlerTest{
    @Test(expected = IllegalArgumentException.class)
    public void wrong_difficulty_exception(){
        AIPlayer ai = AIHandler.createAi("not_a_difficulty", 0, "joe", AITestUtil.dummy_ids(3));
    }

    @Test
    public void make_easy(){
        AIPlayer ai = AIHandler.createAi("easy", 0, "joe", AITestUtil.dummy_ids(3));
        Assert.assertTrue(ai instanceof EasyAIPlayer);
    }

    @Test
    public void make_medium_load(){
        AITestUtil.setAIDirs();
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(1));
        Communication comm = new Communication(CommType.PROMISE_BETRAY, 0, 1);
        ai.receiveMessage(0, comm);
        ArrayList<Action> action = ai.round_action();
        ActionType actual = action.get(0).getAction();
        ActionType ans = ActionType.COOPERATE;
        Assert.assertEquals(ans, actual);
    }

    @Test
    public void make_medium_fresh(){
        AIHandler.setDataPathForTesting("./non_exist");
        AIHandler.setModelPathForTesting("./non_exist");
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(3));
        Assert.assertTrue(ai instanceof TrainableAIPlayer);
    }

    @Test
    public void make_hard_load(){
        AITestUtil.setAIDirs();
        AIPlayer ai = AIHandler.createAi("hard", 0, "joe", AITestUtil.dummy_ids(1));
        Communication comm = new Communication(CommType.PROMISE_BETRAY, 0, 1);
        ai.receiveMessage(0, comm);
        ArrayList<Action> action = ai.round_action();
        ActionType actual = action.get(0).getAction();
        ActionType ans = ActionType.COOPERATE;
        Assert.assertEquals(ans, actual);
    }

    @Test
    public void make_hard_fresh(){
        AIHandler.setDataPathForTesting("./non_exist");
        AIHandler.setModelPathForTesting("./non_exist");
        AIPlayer ai = AIHandler.createAi("hard", 0, "joe", AITestUtil.dummy_ids(3));
        Assert.assertTrue(ai instanceof TrainableAIPlayer);
    }

    @Test
    public void test_train_base_policies(){
        AITestUtil.setAIDirs();
        AIHandler.trainBasePolicies();

        File data_dir = new File(AIHandler.model_path);
        File[] files = data_dir.listFiles();
        HashSet<String> names = new HashSet<String>();
        for(File file: files){names.add(file.getName());}

        HashSet<String> ans = new HashSet<>();
        ans.add("medium.model");
        ans.add("hard.model");
        Assert.assertEquals(ans, names);
    }


}

