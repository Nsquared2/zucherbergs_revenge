import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void basicActionTest() {
        for (ActionType type : ActionType.values()) {
            Action a = new Action(type);
            assertEquals(type.toString(), a.toString());
        }
    }
}
