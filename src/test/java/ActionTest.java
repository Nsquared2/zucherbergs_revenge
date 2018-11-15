import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void basicActionTest() {
        for (ActionType type : ActionType.values()) {
            Action a = new Action(type);
            assertEquals(type.toString(), a.toString());
        }
        Action act = new Action(ActionType.COOPERATE, 1);
        assertEquals(ActionType.COOPERATE.toString(), act.toString());

        act = new Action(ActionType.BETRAY, 1, 1);
        assertEquals(ActionType.BETRAY.toString(), act.toString());
    }
}
