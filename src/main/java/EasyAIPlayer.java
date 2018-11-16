import com.google.common.collect.ArrayListMultimap;
import weka.core.DenseInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/**
 * Basic AI player class that corresponds to the Easy difficulty.
 * This player has random behavior and does not need to be trained by the AI handler.
 */
public class EasyAIPlayer extends AIPlayer{
    private Random rand = new Random();
    private float comm_thresh = 0.9f;

    EasyAIPlayer(int id, String name, ArrayList<Integer> ids){
        super(id, name, ids);
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
        int num_actions = ActionType.values().length;
        ArrayList<Action> actions = new ArrayList<Action>(0);

        for(int reciever: this.enemy_ids) {
            ActionType action_type = ActionType.values()[rand.nextInt(num_actions)];

            Action action = new Action(action_type, this.id, reciever);
            actions.add(action);
        }

        return actions;
    }

    /**
     *Easy player does not update policy so this function just stores round results
     */
    @Override
    void update_policy(ArrayList<HashMap<Integer, ActionType>> round_results){
        int round_id = rcv_comms.size();
        for(int key: this.enemy_ids){
            //Make instance from round data
            Collection<Communication> comms = this.rcv_comms.get(round_id).get(key);
            DenseInstance instance = WekaData.makeInstance(comms);

            //Update round history
            this.round_instances.add(instance);
        }

        //Add new layer to rcv comms for next round
        addMapLayer();
    }
}
