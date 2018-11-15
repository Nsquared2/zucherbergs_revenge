import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

/**
 * This class represents a specific game session between players
 * A session has a unique session ID, as well as a session name.
 * The session stores a map using Players as the key, and WebSocket sessions as the values, in order to
 * communicate back to the players' web clients.
 */
public class GameSession {
    private int sessionId;
    private String sessionName;
    private String privateCode;
    private int roundTime;
    private int numOfHumans;
    private int numOfAI;
    private Map<Player, Session> playerMap;
    private List<AIPlayer> aiPlayers;

    private AIHandler aiHandler;

    private int totalRounds;
    private int currentRound;

    public GameSession(String sessionName, int numOfHumans, int numOfAI) {
        this.sessionName = sessionName;
        this.numOfHumans = numOfHumans;
        this.numOfAI = numOfAI;
        this.totalRounds = 5;
        sessionId = new Random().nextInt(Integer.MAX_VALUE);
        playerMap = new HashMap<>();
        aiPlayers = new ArrayList<>(numOfAI);

        //TODO: Add AI players with the AI Handler
    }

    public int getSessionId() {
        return sessionId;
    }

    /**
     * Sets the optional round time parameter for the game session
     */
    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
    }

    /**
     * Sets the optional private code for the session
     */
    public void setPrivateCode(String code) {
        this.privateCode = code;
    }

    public void setTotalRounds(int numRounds) {
        this.totalRounds = numRounds;
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

    public void endGame() {
        //TODO add code for AI reinforcement learning
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
