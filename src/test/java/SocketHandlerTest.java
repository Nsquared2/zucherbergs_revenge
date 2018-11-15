import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SocketHandlerTest {
    @Test
    public void onCloseTest() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onClose(2, "test");
        assertEquals("Close: statusCode=" + 2 + ", reason=" + "test", bytes.toString().trim());
    }

    @Test
    public void onErrorTest() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onError(new Throwable());
        assertEquals("Error: null", bytes.toString().trim());
    }

    @Test
    public void onMessageTest() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onMessage("TestMessage");

        assertEquals("Message: TestMessage", bytes.toString().trim());
    }

    @Test
    public void testParseMessage() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        handler.setCurrentSess(new TestSession());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onMessage("message 0 declare_betray");

        handler.onConnect(new TestSession());

        int id = handler.idToSessionMap.keySet().iterator().next();
        handler.onMessage("message " + id + " request_cooperate");
        assertTrue(bytes.toString().contains("Message send failed"));
    }
}
