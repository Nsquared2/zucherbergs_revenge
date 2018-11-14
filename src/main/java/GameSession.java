import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameSession {
    private int sessionId;
    private String sessionName;
    private Map<Player, Session> playerMap;

    public GameSession() {
        sessionId = new Random().nextInt();
        playerMap = new HashMap<>();
    }

    public void addPlayer(Player p) {
        playerMap.put(p, p.getWebSocketSession());
    }

    public boolean isRoundOver() {
        boolean isOver = true;
        for (Player p : playerMap.keySet()) {
            if (!p.isConfirmed()) {
                isOver = false;
            }
        }
        return isOver;
    }

    public void updateScores() {
        for (Player p : playerMap.keySet()) {
            //TODO: Gather actions, find action-pair and update points accordingly
        }
    }

    public Player getPlayerForId(int id) {
        for (Player p : playerMap.keySet()) {
            if (p.getPlayerId() == id) {
                return p;
            }
        }
        return null;
    }
}
