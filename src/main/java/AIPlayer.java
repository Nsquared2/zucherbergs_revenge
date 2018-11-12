import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import weka.core.Instance;
/**
 * This class stores basic information about an AI Player and allows actions/messages to be retrieved from the player.
 * This is an abstract class, so the different AI difficulties will implement the message/action/policy updating in different ways.
 */
public abstract class AIPlayer extends Object{
    int id;
    String name;
    ArrayList<Integer> enemy_ids;
    int num_enemies;
    ArrayList<Multimap<Integer, Communication>> rcv_comms;
    Multimap<Integer, Communication> map_template;

    int score;
    ArrayList<ArrayList<String>> action_history;
    Map<Integer, PlayerState> game_state = new HashMap<Integer, PlayerState>();

    AIPlayer(int id, String name, ArrayList<Integer> ids){
        this.id = id;
        this.name = name;
        this.enemy_ids = ids;
        this.enemy_ids.remove(Integer.valueOf(id));
        this.num_enemies = enemy_ids.size();

        this.rcv_comms = new ArrayList<Multimap<Integer, Communication>>();

    }

    /**
     * Receives a message from another player for the round and stores it
     */
    void receiveMessage(int round_id, Communication msg){
        if(round_id+1 > rcv_comms.size()){
            this.rcv_comms.add(ArrayListMultimap.create(this.map_template));
        }

        this.rcv_comms.get(round_id).put(msg.sender_id, msg);
    }

    /**
     * Returns the Communication that the AI would like to send.
     */
    abstract Communication message_action();

    /**
     * Returns a list of Actions that the AI would like to perform,
     * corresponding to the input list of other Player IDs.
     */
    abstract ArrayList<Action> round_action();

    /**
     * Updates the AI's current decision making policy.
     */
    abstract void update_policy();
}
