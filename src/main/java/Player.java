import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerName;
    private int playerId;
    private Session webSocketSession;
    private List<Action> currentActions;
    private boolean turnConfirmed;
    private int currentScore;
    private GameSession gameSession;

    public Player() {}

    public Player (String playerName, int playerId, Session webSocketSession, GameSession gameSession) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.webSocketSession = webSocketSession;
        this.gameSession = gameSession;

        this.currentActions = new ArrayList<>();
        this.turnConfirmed = false;
        this.currentScore = 0;
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

    public void sendMessage(CommType comm, int senderId) {
        try {
            webSocketSession.getRemote().sendString("message " + senderId + " " + comm.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
