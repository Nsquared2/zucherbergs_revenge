import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTest {
    @Test
    public void basicMessageTest() {
        Communication c = new Communication(CommType.PROMISE_BETRAY, 1, 2);
        c.set_sender(2);
        c.set_reciever(1);

        assertEquals(1, c.get_reciever());
        assertEquals(2, c.get_sender());
    }
}
