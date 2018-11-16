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
    public Map<Integer, GameSession> currentGames = new HashMap<>();

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
            session.getRemote().sendString("client_id " + id);
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
        List<String> strings = Arrays.asList(message.split(" "));

        // Ensures that the message code and number of params are valid
        if (strings.get(0).equals("message") && strings.size() == 3) {
            parseMessage(strings.get(1), strings.get(2));
        } else if (strings.get(0).equals("action") && strings.size() == 3) {
            parseAction(strings.get(1), strings.get(2));
        } else if (strings.get(0).equals("new_game")) {
            parseNewGame(strings.subList(1, strings.size()));
        } else if (strings.get(0).equals("player") && strings.size() == 4) {
            parseAddPlayer(strings.get(1), strings.get(2), strings.get(3));
        } else if (strings.get(0).equals("confirm") && strings.size() == 2) {
            parseConfirmation(strings.get(1));
        } else {
            // Invalid messages result in System.out tracking
            // TODO: Switch to a logging framework?
            System.out.println("Invalid message from client");
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
        if (input.size() >= 6) {
            String name = input.get(1);
            int numHumans = Integer.parseInt(input.get(3));
            int numAIs = Integer.parseInt(input.get(5));
            GameSession g = new GameSession(name, numHumans, numAIs);
            currentGames.put(g.getSessionId(), g);
        }

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

        try {
            socketSess.getRemote().sendString("player_id " + id);
        } catch (IOException e) {
            System.out.println("Message send failed");
        }
    }

    /**
     * This method parses action communications from the client, which requires us to update the player
     * state. The player is updated to show that they will be performing a certain action against the given
     * player ID, and a response is sent to the client to confirm the change in action.
     */
    private void parseAction(String playerId, String actionType) {
        int id = Integer.parseInt(playerId);
        try {
            if (playerMap.get(id) != null) {
                playerMap.get(id).updateAction(id, ActionType.valueOf(actionType.toUpperCase()));
                currentSess.getRemote().sendString("updated move for player: " + playerId + " to be: " + actionType);
            }
            if (id == 1 || id == 2 || id == 3) {
                currentSess.getRemote().sendString("updated move for player: " + playerId + " to be: " + actionType);
            }
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
            // id 0,1,2,3 are the test cases
            if (id == 0 || id == 1 || id == 2 || id == 3) {
                currentSess.getRemote().sendString("message " + playerId + " server " + message);
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

    public Player getPlayerForId(int id) {
        return playerMap.get(id);
    }

    public GameSession getGameForId(int id) {
        return currentGames.get(id);
    }
}
