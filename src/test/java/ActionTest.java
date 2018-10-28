import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void basicActionTest() {
        Action a = new Action(ActionType.BETRAY);
        assertEquals("betray", a.toString());
    }
}
