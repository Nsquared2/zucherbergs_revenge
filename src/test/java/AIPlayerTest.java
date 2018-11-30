import org.junit.Test;

public class AIPlayerTest {
    @Test
    public void test_recieve_comms(){
        AIPlayer ai = AIHandler.createAi("easy", 0, "joe", AITestUtil.dummy_ids(1));
        Communication comm = new Communication(CommType.PROMISE_BETRAY, 0, 1);
        ai.receiveMessage(0, comm);
    }

    @Test
    public void round_action_test_easy(){
        AIPlayer ai = AIHandler.createAi("easy", 0, "joe", AITestUtil.dummy_ids(1));
        ai.round_action();

    }

    @Test
    public void round_action_test_trainable(){
        AITestUtil.setAIDirs();
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(1));
        ai.round_action();
    }

}
