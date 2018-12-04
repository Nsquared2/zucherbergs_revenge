import javafx.util.Pair;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        this.currentRound = 1;
        sessionId = new Random().nextInt();
        playerMap = new HashMap<>();
        aiPlayers = new ArrayList<>(numOfAI);

        //TODO: Add AI players with the AI Handler
        for (int i = 0; i < numOfAI; i++) {
            Random r = new Random();
            int ai_id = r.nextInt();
            aiPlayers.add(AIHandler.createAi("easy", ai_id, "AI_" + i, new ArrayList<>()));
        }

        updateAIEnemyList();
    }

    private void updateAIEnemyList() {
        for (AIPlayer ai : aiPlayers) {
            for (AIPlayer other : aiPlayers) {
                if (!other.equals(ai)) {
                    ai.addEnemy(other.getId());
                }
            }
        }
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
        for (AIPlayer ai : aiPlayers) {
            ai.addEnemy(p.getPlayerId());
        }
        for (Session s : playerMap.values()) {
            try {
                s.getRemote().sendString("player_joined " + p.getPlayerId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method determines if the round is over.
     * A round is ended once all of the players in the session have confirmed their moves
     */
    public boolean isRoundOver() {
        if (isGameOver()) {
            return true;
        }

        boolean isOver = true;
        if (playerMap.keySet().size() < numOfHumans) {
            return false;
        }

        for (Player p : playerMap.keySet()) {
            if (!p.isConfirmed()) {
                isOver = false;
            }
        }

        if (isOver) {
            endRound();
            currentRound++;
            sendNewRound();
        }

        if (isGameOver()) {
            endGame();
        }
        return isOver;
    }

    private void sendNewRound() {
        for (Player p : playerMap.keySet()) {
            try {
                p.getWebSocketSession().getRemote().sendString("round_number " + currentRound);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void endRound() {
        for (AIPlayer ai : aiPlayers) {
            ai.round_action();
        }

        updateScores();
        for (Player p: playerMap.keySet()) {
            p.sendScoreUpdate();
        }
    }

    /**
     * This method determines if the game session has ended
     * A game ends once the number of rounds elapsed has passed the setting for total # of rounds
     */
    public boolean isGameOver() {
        return currentRound > totalRounds;
    }

    public void endGame() {
        //TODO add code for AI reinforcement learning
        for (Player p : playerMap.keySet()) {
            try {
                List<Pair<Integer, String>> finalResults = new ArrayList<>();
                for (Player player : playerMap.keySet()) {
                    Pair<Integer, String> result = new Pair<>(player.getCurrentScore(), player.getPlayerName());
                    finalResults.add(result);
                }
                for (AIPlayer ai : aiPlayers) {
                    Pair<Integer, String> result = new Pair<>(ai.score, ai.getName());
                    finalResults.add(result);
                }

                Collections.sort(finalResults, Comparator.comparing(pair -> -pair.getKey()));

                String resultString = finalResults.stream()
                        .map(pair -> pair.getKey() + " " + pair.getValue())
                        .collect(Collectors.joining(" "));

                p.getWebSocketSession().getRemote().sendString("final_scores " + resultString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method goes through all of the players in the session and updates their scores for the next round
     */
    public void updateScores() {
        for (Player p : playerMap.keySet()) {
            updatePlayerScore(p);
            p.resetTurnConfirmation();
        }

        for (AIPlayer ai : aiPlayers) {
            updateAIScore(ai);
        }
    }

    private void updateAIScore(AIPlayer ai) {
        for (Player opponent : playerMap.keySet()) {
            ActionType myAction = ai.getActionForId(opponent.getPlayerId());
            ActionType theirAction = opponent.getActionForId(ai.getId());
            if (myAction == null) {
                myAction = ActionType.IGNORE;
            }
            if (theirAction == null) {
                theirAction = ActionType.IGNORE;
            }
            ai.adjustScore(calculateScore(myAction, theirAction));
        }

        for (AIPlayer otherAI : aiPlayers) {
            if (!otherAI.equals(ai)) {
                ai.adjustScore(calculateScore(
                        ai.getActionForId(otherAI.getId()),
                        otherAI.getActionForId(ai.getId())
                ));
            }
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
     */
    public void updatePlayerScore(Player p) {
        for (Player opponent : playerMap.keySet()) {
            if (!opponent.equals(p)) {
                ActionType myAction = p.getActionForId(opponent.getPlayerId());
                ActionType theirAction = opponent.getActionForId(p.getPlayerId());
                if (myAction == null) {
                    myAction = ActionType.IGNORE;
                }
                if (theirAction == null) {
                    theirAction = ActionType.IGNORE;
                }
                p.adjustScore(calculateScore(myAction, theirAction));
            }
        }

        for (AIPlayer ai : aiPlayers) {
            p.adjustScore(calculateScore(
                    p.getActionForId(ai.getId()),
                    ai.getActionForId(p.getPlayerId())
            ));
        }


    }


    public int calculateScore(ActionType myAction, ActionType theirAction) {
        switch(myAction) {
            case COOPERATE:
                switch(theirAction) {
                    case COOPERATE:
                        return 1;
                    case BETRAY:
                        return 0;
                    case IGNORE:
                        return 1;
                }
            case BETRAY:
                switch(theirAction) {
                    case COOPERATE:
                        return 2;
                    case BETRAY:
                        return 0;
                    case IGNORE:
                        return 1;
                }
            case IGNORE:
                return 0;

            default:
                    return 1;
        }
    }

    public void setId(int id) {
        this.sessionId = id;
    }

    public String getName() {
        return sessionName;
    }

    public int getMaxOcc() {
        return numOfHumans + numOfAI;
    }

    public int getCurrentOcc() {
        return playerMap.size() + aiPlayers.size();
    }

    public boolean isFull() {
        return getCurrentOcc() == getMaxOcc();
    }

    public List<Integer> getIdsForAI() {
        return aiPlayers.stream().map(AIPlayer::getId).collect(Collectors.toList());
    }

    public int getNumRounds() {
        return totalRounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void sendPlayerInfoFor(Player p) {
        System.out.println("Sending player info");

        for (Player opp : playerMap.keySet()) {
            if (!opp.equals(p)) {
                String playerInfo = "player " + opp.getPlayerId() + " " + opp.getPlayerName();
                try {
                    p.getWebSocketSession().getRemote().sendString(playerInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (AIPlayer ai : aiPlayers) {
            String playerInfo = "player " + ai.getId() + " " + ai.getName();
            System.out.println(playerInfo);
            try {
                p.getWebSocketSession().getRemote().sendString(playerInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removePlayer(Player p) {
        playerMap.remove(p);
        for (AIPlayer ai : aiPlayers) {
            ai.removeEnemy(p.getPlayerId());
        }
    }
}
