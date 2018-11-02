import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * This class stores basic information about an AI Player and allows actions/messages to be retrieved from the player.
 * This is an abstract class, so the different AI difficulties will implement the message/action/policy updating in different ways.
 */
public abstract class AIPlayer {
    int id;
    String name;

    int score;
    ArrayList<ArrayList<String>> action_history;
    Map<Integer, PlayerState> game_state = new HashMap<Integer, PlayerState>();

    AIPlayer(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the Communication that the AI would like to send.
     */
    abstract Communication message_action(ArrayList<Integer> ids);

    /**
     * Returns a list of Actions that the AI would like to perform,
     * corresponding to the input list of other Player IDs.
     */
    abstract ArrayList<Action> round_action(ArrayList<Integer> ids);

    /**
     * Updates the AI's current decision making policy.
     */
    abstract void update_policy(ArrayList<Integer> ids);
}
