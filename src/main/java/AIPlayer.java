import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import weka.core.Instances;

/**
 * This class stores basic information about an AI Player and allows actions/messages to be retrieved from the player.
 * This is an abstract class, so the different AI difficulties will implement the message/action/policy updating in different ways.
 */
public abstract class AIPlayer extends Object{
    protected int id;
    private String name;
    protected ArrayList<Integer> enemy_ids;
    protected int num_enemies;
    protected ArrayList<Multimap<Integer, Communication>> rcv_comms;
    protected Instances round_instances;
    protected Instances eval_data;
    protected Multimap<Integer, Communication> map_template;

    int score;
    ArrayList<ArrayList<String>> action_history;
    Map<Integer, PlayerState> game_state = new HashMap<Integer, PlayerState>();

    Map<Integer, ActionType> currentRoundActions = new HashMap<>();

    AIPlayer(){}

    AIPlayer(int id, String name, ArrayList<Integer> ids){
        this.id = id;
        this.name = name;
        this.enemy_ids = ids;
        this.enemy_ids.remove(Integer.valueOf(id));
        this.num_enemies = enemy_ids.size();

        this.rcv_comms = new ArrayList<Multimap<Integer, Communication>>();
        this.round_instances = WekaData.makeDataset();
        this.eval_data = WekaData.makeDataset();

        addMapLayer();

    }

    /**
     * Return action that maximizes value given predicition of enemy action
     * @param enemy_action predicted enemy action
     * @return Action that maximizes value for AI
     */
    ActionType maximizeValue(ActionType enemy_action){
        //TODO: @Vincent Redo action table
        //TODO: @Mike create maximize_reward and minimize_risk modes
        switch(enemy_action){
            case COOPERATE:
                return ActionType.COOPERATE;
            case BETRAY:
                return ActionType.IGNORE;
            case IGNORE:
                return ActionType.COOPERATE;
            default:
                return ActionType.IGNORE;
        }
    }

    /**
     * Adds new round layer to rcv_comms
     */
    protected void addMapLayer(){
        ArrayListMultimap<Integer, Communication> map_layer = ArrayListMultimap.create();
        for(int id: this.enemy_ids){
            map_layer.containsKey(id);
        }
        this.rcv_comms.add(map_layer);
    }

    /**
     * Receives a message from another player for a specified round and stores it
     * @param round_id Id for current round
     * @param msg Message to be recieved
     */
    void receiveMessage(int round_id, Communication msg){
        this.rcv_comms.get(round_id).put(msg.sender_id, msg);
    }

    /**
     * recieves message into most recent round buffer
     * @param msg message to be stored
     */
    void receiveMessage(Communication msg){
        int last_index = this.rcv_comms.size()-1;
        this.rcv_comms.get(last_index).put(msg.sender_id, msg);
    }

    /**
     * Returns name of player
     */
    String getName(){return this.name;}

    /**
     * Returns id of player
     */
    int getId(){return this.id;}

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
     * Updates the AI's current decision making policy and store round_results in round_actions
     */
    abstract void update_policy(HashMap<Integer, ActionType> round_results);

    /**
     * For Testing. Allows access to rcv_comms
     * @return reference to rcv_comms
     */
    public ArrayList<Multimap<Integer, Communication>> getRcvComms(){
       return this.rcv_comms;
    }

    public void addEnemy(int id) {
        enemy_ids.add(id);
    }

    public ActionType getActionForId(int id) {
        return currentRoundActions.get(id);
    }
}
