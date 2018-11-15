import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommunicationTest {
    @Test
    public void basicCommTest() {
        CommType type = CommType.PROMISE_BETRAY;
        Communication c = new Communication(type, 1, 1);
        assertEquals(type.toString(), c.toString());
        assertEquals(type, c.getAction());

        assertEquals("You have received a promise for betrayal", type.getMessage());
    }
}
