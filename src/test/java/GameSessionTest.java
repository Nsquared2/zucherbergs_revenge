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
        GameSession game = new GameSession("Test", 1, 1);
        Player p = new Player("Bob", 1, new TestSession(), game);
        game.setTotalRounds(3);

        game.addPlayer(p);

        p.updateAction(game.getIdsForAI().get(0), ActionType.COOPERATE);
        p.confirmTurn();

        assertFalse(game.isRoundOver());
        assertFalse(game.isGameOver());

        game = new GameSession("Test", 2, 2);
        Player frank = new Player("Frank", 3, new TestSession(), game);
        game.addPlayer(frank);

        frank.confirmTurn();

        assertFalse(game.isRoundOver());
        assertFalse(game.isGameOver());

        game.endGame();
    }

    @Test
    public void testUpdatingScoresAndGettingPlayers() {
        GameSession game = new GameSession("Test", 3, 0);
        Player bob = new Player("Bob", 3, new TestSession(), game);
        Player frank = new Player("Frank", 4, new TestSession(), game);
        Player sally = new Player("Sally", 5, new TestSession(), game);
        game.addPlayer(bob);
        game.addPlayer(frank);
        game.addPlayer(sally);

        bob.updateAction(4, ActionType.COOPERATE);
        frank.updateAction(3, ActionType.COOPERATE);

        bob.confirmTurn();
        frank.confirmTurn();
        sally.confirmTurn();

        assertEquals(2, bob.getCurrentScore());
        assertEquals(2, frank.getCurrentScore());
    }
}
