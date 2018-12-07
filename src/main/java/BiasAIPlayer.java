import weka.core.DenseInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.lang.Math;

/**
 * Basic AI player class that corresponds to the Bias difficulty.
 * This player has random behavior and does not need to be trained by the AI handler.
 */
public class BiasAIPlayer extends AIPlayer{
    private Random rand = new Random();
    private float comm_thresh = 0.9f;
    private double bias;
    private CommType bias_comm;
    private ActionType bias_action;
    private HashMap<Integer, Boolean> active;

    BiasAIPlayer(int id, String name, ArrayList<Integer> ids){
        super(id, name, ids);
        this.bias = 0.65;
        int num_coms = CommType.values().length;
        this.bias_comm = CommType.values()[rand.nextInt(num_coms)];
        int num_actions = ActionType.values().length;
        this.bias_action = ActionType.values()[rand.nextInt(num_actions)];

        this.active = new HashMap<>();
        for(int enemy: this.enemy_ids){
            this.active.put(enemy, false);
        }

    }

    /**
     * Returns a new Communication that will be sent to a random player, and has a random communication type.
     */
    Communication message_action(){
        int num_coms = CommType.values().length;
        CommType comm_type;
        int receiver = this.enemy_ids.get(rand.nextInt(this.enemy_ids.size()));

        if(Math.random() > this.bias) {
            comm_type = this.bias_comm;
            this.active.put(receiver, true);
        }
        else
            comm_type = CommType.values()[rand.nextInt(num_coms)];

        Communication comm = new Communication(comm_type, this.id, receiver);
        return comm;
    }

    /**
     * Creates a random Action for each of the Player IDs given as input.
     */
    HashMap<Integer, Action> round_action() {
        currentRoundActions.clear();

        int num_actions = ActionType.values().length;
        HashMap<Integer, Action> actions = new HashMap<>();

        for(int reciever: this.enemy_ids) {
            ActionType action_type;

            if(this.active.get(reciever))
                action_type = this.bias_action;
            // TODO: change back to 0.9 once messages are implemented
            else if (Math.random() > this.bias)
                action_type = this.bias_action;
            else
                action_type = ActionType.values()[rand.nextInt(num_actions)];

            Action action = new Action(action_type, this.id, reciever);
            actions.put(reciever, action);
            currentRoundActions.put(reciever, action_type);
        }

        return actions;
    }

    /**
     *Bias player does not update policy so this function just stores round results
     */
    @Override
    void update_policy(HashMap<Integer, ActionType> round_results){
        int round_id = rcv_comms.size()-1;
        for(int key: this.enemy_ids){
            //Make instance from round data
            Collection<Communication> comms = this.rcv_comms.get(round_id).get(key);
            ActionType enemy_action = round_results.get(key);
            DenseInstance instance = WekaData.makeInstance(comms, enemy_action, this.round_instances);

            //Update round history
            this.round_instances.add(instance);
        }

        //Add new layer to rcv comms for next round
        addMapLayer();
        for(int enemy: this.enemy_ids){
            this.active.put(enemy, false);
        }
    }
}

