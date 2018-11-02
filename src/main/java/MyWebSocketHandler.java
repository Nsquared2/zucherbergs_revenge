import java.io.IOException;
import java.util.*;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * This class is responsible for handling communications as they come in from the web clients.
 * Communication is performed over the WebSocket API, so we need to use the WebSocket handler interface
 */
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

    /**
     * This method contains any behavior that needs to be performed when a new client connects to the server.
     * Currently, we generate a new Player ID, and broadcast that ID to the already connected players.
     * We also send a basic response to the connecting session, to acknowledge that we have noticed them.
     * @param session
     */
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

    /**
     * Generates a new random Player ID, and stores the session with the ID as the key.
     * @param session
     * @return
     */
    private int generatePlayerId(Session session) {
        int id = new Random().nextInt();
        playerMap.put(id, session);
        return id;
    }

    /**
     * Handles new incoming messages from the clients. A message does not come with an associated Session,
     * so we need to handle that during parsing.
     * @param message
     */
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

    /**
     * This method is responsible for parsing the raw communication coming from the client
     * Depending on the first word, which denotes the type of communication, the rest of the
     * message will be sent to a different parser.
     */
    private void parseCommunication(String message) {
        List<String> strangs = Arrays.asList(message.split(" "));
        if (strangs.get(0).equals("message")) {
            parseMessage(strangs.get(1), strangs.get(2));
        } else if (strangs.get(0).equals("action")) {
            parseAction(strangs.get(1), strangs.get(2));
        }
    }

    /**
     * This method parses action communications from the client, which requires us to update the player
     * state. The player is updated to show that they will be performing a certain action against the given
     * player ID, and a response is sent to the client to confirm the change in action.
     */
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

    /**
     * This method parses message communications, where a player wants to send a comm to some other player.
     * The communication type is taken, and we send a response to the specified client session with the
     * message text corresponding to the desired communication type.
     */
    private void parseMessage(String playerId, String commType) {
        int id = Integer.parseInt(playerId);
        CommType comm = CommType.valueOf(commType);
        String message;

        if (comm != null) {
            message = comm.getMessage();
        } else {
            message = commType;
            //message = "Error, unknown communication type";
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
