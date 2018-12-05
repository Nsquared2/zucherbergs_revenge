import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerTest {

    @Test
    public void basicPlayerTest() {
        GameSession game = new GameSession("Test", 3, 3,
                5, "easy", false);
        Player p = new Player("Bob", 3, new TestSession(), game);

        p.confirmTurn();
        assertTrue(p.isConfirmed());

        assertTrue(p.getWebSocketSession() instanceof TestSession);

        assertEquals(3, p.getPlayerId());
    }

    @Test
    public void testPlayerActions() {
        GameSession game = new GameSession("Test", 3,
                3, 5, "easy", false);
        Player p = new Player("Bob", 3, new TestSession(), game);
        Player p2 = new Player("Frank", 4, new TestSession(), game);

        p.updateAction(4, ActionType.BETRAY);

        assertEquals(ActionType.BETRAY, p.getActionForId(4));
    }

    @Test
    public void testPlayerMessaging() {
        GameSession game = new GameSession("Test", 3, 3,
                5, "easy", false);
        Player p = new Player("Bob", 3, new TestSession(), game);
        Player p2 = new Player("Frank", 4, new TestSession(), game);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        p.sendMessage(CommType.PROMISE_BETRAY, 4);

        assertTrue(bytes.toString().contains(CommType.PROMISE_BETRAY.getMessage()));
    }
}
