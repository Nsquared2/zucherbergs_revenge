import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        assertTrue(bytes.toString().contains("Message: TestMessage"));
    }

    @Test
    public void testParseMessage() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        handler.setCurrentSess(new TestSession());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onMessage("message 0 declare_betray");

        handler.onConnect(new TestSession());

//        int id = handler.idToSessionMap.keySet().iterator().next();
//        handler.onMessage("message " + id + " request_cooperate");
//        assertTrue(bytes.toString().contains("Message send failed"));
    }

    @Test
    public void testParseAction() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        handler.setCurrentSess(new TestSession());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onConnect(new TestSession());

//        int id = handler.idToSessionMap.keySet().iterator().next();
//
//        handler.onMessage("action " + id + " BETRAY");
    }

    @Test
    public void testParseNewGame() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        handler.setCurrentSess(new TestSession());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onMessage("new_game name testGame humans 2 ai 2");

        GameSession game = handler.currentGames.values().iterator().next();

        assertEquals(game, handler.getGameForId(game.getSessionId()));
    }

    @Test
    public void testParseAddPlayer() {

        MyWebSocketHandler handler = new MyWebSocketHandler();
        handler.setCurrentSess(new TestSession());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onMessage("new_game name testGame humans 2 ai 2");
        handler.onConnect(new TestSession());

        GameSession game = handler.currentGames.values().iterator().next();
//        int playerId = handler.idToSessionMap.keySet().iterator().next();
//
//        handler.onMessage("player testPlayer " + playerId + " " + game.getSessionId());
//
//        assertNotNull(handler.getPlayerForId(playerId));
    }

    @Test
    public void testInvalidMessage() {
        MyWebSocketHandler handler = new MyWebSocketHandler();
        handler.setCurrentSess(new TestSession());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        handler.onMessage("nonsense message");

        assertTrue(bytes.toString().contains("Invalid message from client"));
    }
}
