import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class represents a specific game session between players
 * A session has a unique session ID, as well as a session name.
 * The session stores a map using Players as the key, and WebSocket sessions as the values, in order to
 * communicate back to the players' web clients.
 */
public class GameSession {
    private int sessionId;
    private String sessionName;
    private Map<Player, Session> playerMap;

    private int totalRounds;
    private int currentRound;

    public GameSession() {
        sessionId = new Random().nextInt();
        playerMap = new HashMap<>();
    }

    /**
     * This method adds a new Player to the game session, storing the Player-Session pair in the map
     */
    public void addPlayer(Player p) {
        playerMap.put(p, p.getWebSocketSession());
    }

    /**
     * This method determines if the round is over.
     * A round is ended once all of the players in the session have confirmed their moves
     */
    public boolean isRoundOver() {
        boolean isOver = true;
        for (Player p : playerMap.keySet()) {
            if (!p.isConfirmed()) {
                isOver = false;
            }
        }
        if (isOver) {
            currentRound++;
        }
        return isOver;
    }

    /**
     * This method determines if the game session has ended
     * A game ends once the number of rounds elapsed has passed the setting for total # of rounds
     */
    public boolean isGameOver() {
        return currentRound >= totalRounds;
    }

    /**
     * This method goes through all of the players in the session and updates their scores for the next round
     */
    public void updateScores() {
        for (Player p : playerMap.keySet()) {
            updatePlayerScore(p);
        }
    }

    /**
     * Retrieves a Player based on the given player ID
     * Returns null if the player could not be found.
     */
    public Player getPlayerForId(int id) {
        for (Player p : playerMap.keySet()) {
            if (p.getPlayerId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * This method is responsible for updating the score of a specific player, based on their pairs of
     * moves against their opponents.
     *
     * TODO: Currently, we just increment the score by one, need to add actual scoring logic
     */
    public void updatePlayerScore(Player p) {
        for (Player opponent : playerMap.keySet()) {
            if (!opponent.equals(p)) {
                p.adjustScore(1);
            }
        }
    }
}
