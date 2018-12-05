import com.google.common.collect.ArrayListMultimap;
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
    private int bias;

    BiasAIPlayer(int id, String name, ArrayList<Integer> ids){
        super(id, name, ids);
        this.bias = this.rand.nextInt(3);

    }

    /**
     * Returns a new Communication that will be sent to a random player, and has a random communication type.
     */
    Communication message_action(){
        if(rand.nextFloat() > this.comm_thresh){
            int num_coms = CommType.values().length;
            CommType comm_type = CommType.values()[rand.nextInt(num_coms)];
            int receiver = this.enemy_ids.get(rand.nextInt(this.enemy_ids.size()));

            Communication comm = new Communication(comm_type, this.id, receiver);
            return comm;
        }
        else{
            return null;
        }
    }

    /**
     * Creates a random Action for each of the Player IDs given as input.
     */
    ArrayList<Action> round_action() {
        currentRoundActions.clear();

        int num_actions = ActionType.values().length;
        ArrayList<Action> actions = new ArrayList<Action>(0);

        for(int reciever: this.enemy_ids) {
            int action_id;
            if(Math.random() > 0.8)
                action_id = this.bias;
            else
                action_id = rand.nextInt(num_actions);

            ActionType action_type = ActionType.values()[action_id];

            Action action = new Action(action_type, this.id, reciever);
            actions.add(action);
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
    }
}

