import java.util.ArrayList;
import weka.classifiers.Classifier;

/**
 * Basic AI player class that corresponds to the Medium difficulty.
 * This player has deterministic behavior and needs to be trained by the AI handler.
 * Trains using Naive Bayes
 */
public class MediumAIPlayer extends AIPlayer{
    private float comm_thresh = 0.9f;

    MediumAIPlayer(int id, String name){
        super(id, name);
        this.nbayes = model;
    }

    /**
     * Returns a new Communication that will be sent to a random player, and has a random communication type.
     */
    Communication message_action(ArrayList<Integer> ids){
        if(rand.nextFloat() > this.comm_thresh){

        }
        else{
            return null;
        }
    }

    /**
     * Creates a random Action for each of the Player IDs given as input.
     */
    ArrayList<Action> round_action(ArrayList<Integer> ids) {
        int num_actions = ActionType.values().length;
        ArrayList<Action> actions = new ArrayList<Action>(0);

        for(int reciever: ids) {
            ActionType action_type = ActionType.values()[rand.nextInt(num_actions)];

            Action action = new Action(action_type, this.id, reciever);
            actions.add(action);
        }

        return actions;
    }

    void update_policy(ArrayList<Integer> ids){}
}


