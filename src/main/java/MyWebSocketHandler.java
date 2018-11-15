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
    private Map<Integer, Player> playerMap = new HashMap<>();
    public Map<Integer, Session> idToSessionMap = new HashMap<>();
    private Map<Integer, GameSession> currentGames = new HashMap<>();

    /**
     * This is responsible for handling any behavior that needs to occur when a WebSocket conneciton is closed
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    /**
     * This method is responsible for handling errors coming from the WebSocket conneciton itself
     */
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
        try {
            for (Session s : idToSessionMap.values()) {
                s.getRemote().sendString("new player joined: " + id);
            }
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
        idToSessionMap.put(id, session);
        return id;
    }

    /**
     * Handles new incoming messages from the clients. A message does not come with an associated Session,
     * so we need to handle that during parsing.
     * @param message
     */
    @OnWebSocketMessage
    public void onMessage(String message) {
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
        } else if (strangs.get(0).equals("new_game")) {
            parseNewGame(strangs.subList(1, strangs.size()-1));
        } else if (strangs.get(0).equals("player")) {
            parseAddPlayer(strangs.get(1), strangs.get(2), strangs.get(3));
        } else if (strangs.get(0).equals("confirm")) {
            parseConfirmation(strangs.get(1));
        }
    }

    /**
     * This method takes the String player id, finds the corresponding Player in the map, and
     * sets their turn confirmation to true
     */
    private void parseConfirmation(String playerId) {
        int id = Integer.parseInt(playerId);

        Player p = playerMap.get(id);
        p.confirmTurn();
    }

    /**
     * This method is responsible for parsing the new game session parameters, creating a GameSession object,
     * and adding the game to the Map of current games
     */
    private void parseNewGame(List<String> input) {
        String name = input.get(1);
        int numHumans = Integer.parseInt(input.get(3));
        int numAIs = Integer.parseInt(input.get(5));
        GameSession g = new GameSession(name, numHumans, numAIs);
        currentGames.put(g.getSessionId(), g);

        //TODO: Add code to handle optional parameters (private code, round limit)
    }

    /**
     * This method takes in the parameters for adding a player to an existing game session
     * It creates the new Player object with the given params, adds it to the game session, and
     * also stores it in the player map.
     */
    private void parseAddPlayer(String playerName, String playerId, String gameID) {
        int id = Integer.parseInt(playerId);
        int gID = Integer.parseInt(gameID);
        Session socketSess = idToSessionMap.get(id);

        GameSession game = currentGames.get(gID);

        Player p = new Player(playerName, id, socketSess, game);
        game.addPlayer(p);
        playerMap.put(id, p);
    }

    /**
     * This method parses action communications from the client, which requires us to update the player
     * state. The player is updated to show that they will be performing a certain action against the given
     * player ID, and a response is sent to the client to confirm the change in action.
     */
    private void parseAction(String playerId, String actionType) {
        int id = Integer.parseInt(playerId);
        try {
            System.out.println(playerId);
            System.out.println(actionType);
            playerMap.get(id).updateAction(id, ActionType.valueOf(actionType));
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
        String message = commType;

        // Try to find the message text for the given comm type
        for (CommType t : CommType.values()) {
            if (t.toString().equals(commType)) {
                message = t.getMessage();
            }
        }

        try {
            // id 0 is the test case
            if (id == 0) {
                currentSess.getRemote().sendString("message " + "server " + message);
            } else {
                idToSessionMap.get(id).getRemote().sendString(commType);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message send failed");
        }
    }

    public void setCurrentSess(Session s) {
        this.currentSess = s;
    }
}
