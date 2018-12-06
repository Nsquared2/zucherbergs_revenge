import weka.core.DenseInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Basic AI player class that corresponds to the Easy difficulty.
 * This player has random behavior and does not need to be trained by the AI handler.
 */
public class EasyAIPlayer extends AIPlayer{
    private float comm_thresh = 0.9f;

    EasyAIPlayer(int id, String name, ArrayList<Integer> ids){
        super(id, name, ids);
    }

    /**
     * Creates a random Action for each of the Player IDs given as input.
     */
    HashMap<Integer, Action> round_action() {
        currentRoundActions.clear();

        int num_actions = ActionType.values().length;
        HashMap<Integer, Action> actions = new HashMap<>();

        for(int reciever: this.enemy_ids) {
            ActionType action_type = ActionType.values()[rand.nextInt(num_actions)];

            Action action = new Action(action_type, this.id, reciever);
            actions.put(reciever, action);
            currentRoundActions.put(reciever, action_type);
        }

        return actions;
    }

    /**
     *Easy player does not update policy so this function just stores round results
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
