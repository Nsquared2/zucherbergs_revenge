import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class AIFunctional {
    @Test
    public void AI2() {
        ActionType dummy_choice = ActionType.BETRAY;
        AIPlayer ai = AIHandler.createAi("medium", 0, "joe", AITestUtil.dummy_ids(1));
        ActionType correct_action = ai.maximizeValue(dummy_choice);
        ActionType ai_action = ActionType.BETRAY;

        for (int r = 0; r < 2; r++) {
            ArrayList<Action> ai_actions = ai.round_action();
            ActionType action = ai_actions.get(0).getAction();
            boolean correct = (action.equals(correct_action));
            System.out.println(action.toString());
//            System.out.println("ai correct? " + String.valueOf(correct));

            HashMap<Integer, ActionType> dummy_results = new HashMap<>();
            dummy_results.put(1, dummy_choice);
            ai.update_policy(dummy_results);

            ai_action = action;
        }

        Assert.assertEquals(correct_action, ai_action);
    }
}
