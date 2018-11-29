import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to represent a human player in a game session.
 * This object is responsible for tracking the actions, scores, and IDs of the player
 * Communication can go through this object, as it maintains a reference to the WebSocket session
 */
public class Player {
    private String playerName;
    private int playerId;
    private Session webSocketSession;
    private Map<Integer, ActionType> currentActions;
    private boolean turnConfirmed;
    private int currentScore;
    private GameSession gameSession;

    public Player (String playerName, int playerId, Session webSocketSession, GameSession gameSession) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.webSocketSession = webSocketSession;
        this.gameSession = gameSession;

        this.currentActions = new HashMap<>();
        this.turnConfirmed = false;
        this.currentScore = 0;
    }

    public void confirmTurn() {
        turnConfirmed = true;
        gameSession.isRoundOver();
    }

    public void resetTurnConfirmation() {
        turnConfirmed = false;
    }

    public boolean isConfirmed() {
        return turnConfirmed;
    }

    public Session getWebSocketSession() {
        return webSocketSession;
    }

    public int getPlayerId() {
        return playerId;
    }

    /**
     * This method handles sending a message to the current Player object, by using its stored
     * WebSocket session
     * @param comm The Communication that needs to be sent
     * @param senderId The ID of the player that initiated the message being sent
     */
    public void sendMessage(CommType comm, int senderId) {
        try {
            webSocketSession.getRemote().sendString("message " + senderId + " " + comm.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message send failed");
        }
    }

    /**
     * This method updates the action being performed against a specified opponent
     * @param playerId The ID of the player that will receive the action
     * @param action The type of action to be performed at the end of the round
     */
    public void updateAction(int playerId, ActionType action) {
        currentActions.put(playerId, action);
    }

    public ActionType getActionForId(int playerId) {
        return currentActions.get(playerId);
    }

    /**
     * This method handles adjusting the current score of the player by a given amount
     */
    public void adjustScore(int adjustment) {
        currentScore += adjustment;
    }

    public int getCurrentScore() {
        return this.currentScore;
    }

    public void sendScoreUpdate() {
        try {
            webSocketSession.getRemote().sendString("new_score " + currentScore);
        } catch (IOException e) {
            System.out.println("Message send failed");
        }
    }

    public void setGameSession(GameSession game) {
        this.gameSession = game;
    }

    public void setWebSocketSession(Session sess) {
        this.webSocketSession = sess;
    }
}
