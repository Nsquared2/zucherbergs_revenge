import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameSessionTest {

    @Test
    public void basicSessionTest() {
        GameSession game = new GameSession("Test", 2, 2);

        game.setPrivateCode("Private");
        game.setRoundTime(30);

        assertTrue(game.getSessionId() >= 0);
    }

    @Test
    public void testRoundAndGameEnds() {
        GameSession game = new GameSession("Test", 2, 2);
        game.setTotalRounds(1);

        assertTrue(game.isRoundOver());
        assertTrue(game.isGameOver());

        game = new GameSession("Test", 2, 2);
        game.addPlayer(new Player("Bob", 3, new TestSession(), game));

        assertFalse(game.isRoundOver());
        assertFalse(game.isGameOver());

        game.endGame();
    }

    @Test
    public void testUpdatingScoresAndGettingPlayers() {
        GameSession game = new GameSession("Test", 2, 2);
        game.addPlayer(new Player("Bob", 3, new TestSession(), game));
        game.addPlayer(new Player("Frank", 4, new TestSession(), game));

        game.updateScores();
        assertEquals(1, game.getPlayerForId(3).getCurrentScore());
        assertEquals(1, game.getPlayerForId(4).getCurrentScore());

        assertNull(game.getPlayerForId(1));
    }
}
