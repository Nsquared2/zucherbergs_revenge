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

    private static Map<Integer, Player> playerMap = new HashMap<>();
    public static Map<Integer, GameSession> currentGames = new HashMap<>();

    /**
     * This is responsible for handling any behavior that needs to occur when a WebSocket conneciton is closed
     */
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        for (Player p : playerMap.values()) {
            if (p.getWebSocketSession().equals(session)) {
                GameSession game = p.getGameSession();
                if (game != null) {
                    //game.removePlayer(p);
                }
            }
        }
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
        System.out.println("Connection made");
    }

    /**
     * Generates a new random Player ID, and stores the session with the ID as the key.
     * @return
     */
    private int generatePlayerId() {
        int id = new Random().nextInt();
        while (playerMap.keySet().contains(id)) {
            id = new Random().nextInt();
        }
        return id;
    }

    /**
     * Handles new incoming messages from the clients. A message does not come with an associated Session,
     * so we need to handle that during parsing.
     * @param message
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message: " + message);
        parseCommunication(message, session);
    }

    public void onMessage(String message) {

    }

    /**
     * This method is responsible for parsing the raw communication coming from the client
     * Depending on the first word, which denotes the type of communication, the rest of the
     * message will be sent to a different parser.
     */
    private void parseCommunication(String message, Session session) {
        List<String> strings = Arrays.asList(message.split(" "));

        // Ensures that the message code and number of params are valid
        if (strings.get(0).equals("message") && strings.size() == 3) {
            parseMessage(strings.get(1), strings.get(2), session);
        } else if (strings.get(0).equals("action") && strings.size() == 3) {
            parseAction(strings.get(1), strings.get(2), session);
        } else if (strings.get(0).equals("new_game")) {
            parseNewGame(strings.subList(1, strings.size()));
        } else if (strings.get(0).equals("joingame")) {
            parseAddPlayer(strings.get(1), strings.get(2));
        } else if (strings.get(0).equals("confirm")) {
            parseConfirmation(session);
        } else if (strings.get(0).equals("newplayer")) {
            parseNewPlayer(strings.get(1), session );
        } else if (strings.get(0).equals("game_info")) {
            sendGameInfo(session);
        } else if (strings.get(0).equals("update_player")) {
            updatePlayerSession(strings.get(1), session);
        } else if (strings.get(0).equals("join_private")) {
            parseJoinPrivate(strings.get(1), strings.get(2));
        } else {


            // Invalid messages result in System.out tracking
            // TODO: Switch to a logging framework?
            System.out.println("Invalid message from client");
        }
    }

    private void parseJoinPrivate(String playerId, String code) {
        int id = Integer.parseInt(playerId);
        Player p = playerMap.get(id);

        for (GameSession g : currentGames.values()) {
            if (g.getPrivateCode().equals(code)) {
                g.addPlayer(p);
                p.setGameSession(g);

                try {
                    p.getWebSocketSession().getRemote().sendString("game_joined, " + g.getSessionId());
                } catch (IOException e) {
                    System.out.println("Message send failed");
                }
                return;
            }
        }

        try {
            p.getWebSocketSession().getRemote().sendString("game_join_failed");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayerSession(String s, Session session) {
        int playerId = Integer.parseInt(s);
        Player p = playerMap.get(playerId);
        System.out.println("Gotta update player: " + playerId);
        if (p != null) {
            p.setWebSocketSession(session);

            GameSession game = p.getGameSession();
            String gameInfo = "game_info " + game.getSessionId() + " "
                    + game.getName() + " " + game.getMaxOcc() + " "
                     + game.getNumRounds() + " " + game.getCurrentRound();

            try {
                p.getWebSocketSession().getRemote().sendString(gameInfo);
                System.out.println(gameInfo);
                game.sendPlayerInfoFor(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends all of the relevant information for any game sessions that are public and not filled to maximum capacity.
     */
    private void sendGameInfo(Session session) {
        System.out.println("Gotta send game info");
        for (GameSession game : currentGames.values()) {
            if (!game.isFull() && !game.isPrivate()) {
                try {
                    String message = "game, " + game.getSessionId() + ", "
                            + game.getName() + ", " + game.getMaxOcc() + ", " + game.getCurrentOcc()
                            + ", " + game.getNumRounds();
                    System.out.println(message);
                    session.getRemote().sendString(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method takes the String player id, finds the corresponding Player in the map, and
     * sets their turn confirmation to true
     */
    private void parseConfirmation(Session session) {
        Player p = null;
        for (Player player : playerMap.values()) {
            if (player.getWebSocketSession().equals(session)) {
                p = player;
            }
        }

        if (p != null) {
            p.confirmTurn();
        }
    }

    /**
     * This method is responsible for parsing the new game session parameters, creating a GameSession object,
     * and adding the game to the Map of current games
     */
    private void parseNewGame(List<String> input) {
        String name = input.get(0);
        int numRounds = Integer.parseInt(input.get(1));
        int numHumans = Integer.parseInt(input.get(2));
        int numAIs = Integer.parseInt(input.get(3));
        String difficulty = input.get(4);
        boolean bias = false;
        if (input.indexOf("biased") != -1) {
            bias = true;
        }
        GameSession g = new GameSession(name, numHumans, numAIs, numRounds, difficulty, bias);
        currentGames.put(g.getSessionId(), g);

        System.out.println("Added game: " + g.getName() + " and we have " + currentGames.values().size() + " current games");

        if (input.indexOf("time") != -1) {
            g.setRoundTime(Integer.parseInt(input.get(input.indexOf("time") + 1)));
        }
        if (input.indexOf("code") != -1) {
            g.setPrivateCode(input.get(input.indexOf("code") + 1));
        }
    }

    private void parseNewPlayer(String playerName, Session sess) {
        int id = generatePlayerId();
        Player p = new Player(playerName, id, sess, null);
        playerMap.put(id, p);
        try {
            sess.getRemote().sendString("newplayer, " + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes in the parameters for adding a player to an existing game session
     * It creates the new Player object with the given params, adds it to the game session, and
     * also stores it in the player map.
     */
    private void parseAddPlayer(String playerId, String gameID) {
        int id = Integer.parseInt(playerId);
        int gID = Integer.parseInt(gameID);

        Player p = playerMap.get(id);
        GameSession game = currentGames.get(gID);

        if (game == null) {
            try {
                p.getWebSocketSession().getRemote().sendString("game_join_failed");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        game.addPlayer(p);
        p.setGameSession(game);

        try {
            p.getWebSocketSession().getRemote().sendString("game_joined, " + gID);
        } catch (IOException e) {
            System.out.println("Message send failed");
        }
    }

    /**
     * This method parses action communications from the client, which requires us to update the player
     * state. The player is updated to show that they will be performing a certain action against the given
     * player ID, and a response is sent to the client to confirm the change in action.
     */
    private void parseAction(String playerId, String actionType, Session session) {
        int id = Integer.parseInt(playerId);
        try {
            Player p = null;
            for (Player player : playerMap.values()) {
                if (player.getWebSocketSession().equals(session)) {
                    p = player;
                }
            }
            if (p != null) {
                p.updateAction(id, ActionType.valueOf(actionType.toUpperCase()));
                session.getRemote().sendString("updated move for player: " + playerId + " to be: " + actionType);
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
    private void parseMessage(String playerId, String commType, Session session) {
        Player sender = null;
        for (Player player : playerMap.values()) {
            if (player.getWebSocketSession().equals(session)) {
                sender = player;
            }
        }

        int id = Integer.parseInt(playerId);
        String message = commType;

        // Try to find the message text for the given comm type
        for (CommType t : CommType.values()) {
            if (t.toString().equals(commType)) {
                message = t.getMessage();
            }
        }

        try {
            Player p = playerMap.get(id);
            String socketMessage = "message " + id +
                    " " + sender.getPlayerId() + " " + message;
            sender.getWebSocketSession().getRemote().sendString(socketMessage);
            if (p != null) {
                p.getWebSocketSession().getRemote().sendString(socketMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message send failed");
        }
    }

    public Player getPlayerForId(int id) {
        return playerMap.get(id);
    }

    public GameSession getGameForId(int id) {
        return currentGames.get(id);
    }
}
