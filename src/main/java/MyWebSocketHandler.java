import java.io.IOException;
import java.util.*;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class MyWebSocketHandler {

    private Session currentSess;
    private Map<Integer, Session> playerMap = new HashMap<Integer, Session>();

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        int id = generatePlayerId(session);
        currentSess = session;
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {
            for (Session s : playerMap.values()) {
                s.getRemote().sendString("new player joined: " + id);
            }
            session.getRemote().sendString("Hello Webbrowser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int generatePlayerId(Session session) {
        int id = new Random().nextInt();
        playerMap.put(id, session);
        return id;
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        /*
        Message (To, Content)
            Send message to that client with the sender's player id
        Action (To, ActionType)
            Update player state
        Confirmation ()
            Check if everyone's confirmed -> end round
         */

        System.out.println("Message: " + message);
        parseCommunication(message);
    }

    private void parseCommunication(String message) {
        List<String> strangs = Arrays.asList(message.split(" "));
        if (strangs.get(0).equals("message")) {
            parseMessage(strangs.get(1), strangs.get(2));
        } else if (strangs.get(0).equals("action")) {
            parseAction(strangs.get(1), strangs.get(2));
        }
    }

    private void parseAction(String playerId, String actionType) {
        int id = Integer.parseInt(playerId);
        //TODO: Update internal player state with new action towards playerId
        try {
            System.out.println(playerId);
            System.out.println(actionType);
            currentSess.getRemote().sendString("updated move for player: " + playerId + " to be: " + actionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseMessage(String playerId, String commType) {
        int id = Integer.parseInt(playerId);
        CommType comm = CommType.valueOf(commType);
        String message;

        if (comm != null) {
            message = comm.getMessage();
        } else {
            message = "Error, unknown communication type";
        }
        try {
            // id 0 is the test case
            if (id == 0) {
                currentSess.getRemote().sendString("message " + "server " + message);
            } else {
                playerMap.get(id).getRemote().sendString(commType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
