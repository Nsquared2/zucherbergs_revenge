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
    private String privateCode = "";
    private int roundTime;
    private int numOfHumans;
    private int numOfAI;
    private Map<Player, Session> playerMap;
    private List<AIPlayer> aiPlayers;

    private AIHandler aiHandler;
    private String difficulty;

    private int totalRounds;
    private int currentRound;

    public GameSession(String sessionName, int numOfHumans, int numOfAI, int numRounds, String difficulty) {
        this.sessionName = sessionName;
        this.numOfHumans = numOfHumans;
        this.numOfAI = numOfAI;
        this.totalRounds = numRounds;
        this.currentRound = 1;
        sessionId = new Random().nextInt();
        playerMap = new HashMap<>();
        aiPlayers = new ArrayList<>(numOfAI);
        this.difficulty = difficulty;
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

        for (Session s : playerMap.values()) {
            String playerInfo = "player " + p.getPlayerId() + " " + p.getPlayerName();
            try {
                s.getRemote().sendString(playerInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (playerMap.size() >= numOfHumans) {
            generateAIPlayers();
        }
    }

    private void generateAIPlayers() {
        ArrayList<Integer> enemyIds = new ArrayList<>();
        List<Integer> aiIds = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < numOfAI; i++) {
            int ai_id = r.nextInt();
            enemyIds.add(ai_id);
            aiIds.add(ai_id);
        }
        for (Player p : playerMap.keySet()) {
            enemyIds.add(p.getPlayerId());
        }

        for (int i = 0; i < numOfAI; i++) {
            aiPlayers.add(AIHandler.createAi(difficulty, aiIds.get(i), "AI_" + i, new ArrayList<>(enemyIds)));
        }

        for (Player p : playerMap.keySet()) {
            for (AIPlayer ai : aiPlayers) {
                String playerInfo = "player " + ai.getId() + " " + ai.getName();
                try {
                    p.getWebSocketSession().getRemote().sendString(playerInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        for (AIPlayer ai : aiPlayers) {
            ai.update_policy(getActionsTowardsAI(ai.getId()));
        }
    }

    public HashMap<Integer, ActionType> getActionsTowardsAI(int aiId) {
        HashMap<Integer, ActionType> actions = new HashMap<>();
        for (Player p : playerMap.keySet()) {
            actions.put(p.getPlayerId(), p.getActionForId(aiId));
        }
        for (AIPlayer ai : aiPlayers) {
            actions.put(ai.id, ai.getActionForId(aiId));
        }
        return actions;
    }

    /**
     * This method determines if the game session has ended
     * A game ends once the number of rounds elapsed has passed the setting for total # of rounds
     */
    public boolean isGameOver() {
        return currentRound > totalRounds;
    }

    public void endGame() {
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
                try {
                    p.getWebSocketSession().getRemote().sendString("message server " + opponent.getPlayerId() +
                            theirAction.getMessage() + "<br>" + myAction.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (AIPlayer ai : aiPlayers) {
            ActionType myAction = p.getActionForId(ai.getId());
            ActionType theirAction = ai.getActionForId(p.getPlayerId());
            p.adjustScore(calculateScore(
                    myAction,
                    theirAction));
            try {
                p.getWebSocketSession().getRemote().sendString("message server " + ai.getId() + " " +
                        theirAction.getMessage() + "<br>" + myAction.getPerformerMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }


    public int calculatePlace(Player player) {
        int place = 1;
        for (Player p : playerMap.keySet()) {
            if (!p.equals(player) && p.getCurrentScore() > player.getCurrentScore()) {
                place++;
            }
        }
        for (AIPlayer ai : aiPlayers) {
            if (ai.score > player.getCurrentScore()) {
                place++;
            }
        }
        return place;
    }

    public boolean isPrivate() {
        return !privateCode.equals("");
    }

    public String getPrivateCode() {
        return privateCode;
    }
}
