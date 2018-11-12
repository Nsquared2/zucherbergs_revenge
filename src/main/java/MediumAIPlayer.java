import java.util.ArrayList;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;

/**
 * Basic AI player class that corresponds to the Medium difficulty.
 * This player has deterministic behavior and needs to be trained by the AI handler.
 * Trains using Naive Bayes
 */
public class MediumAIPlayer extends AIPlayer{
    private NaiveBayesModel nbayes;
    private float comm_thresh = 0.9f;

    MediumAIPlayer(int id, String name, NaiveBayesModel model){
        super(id, name);
        this.nbayes = model;
    }

    /**
     * Returns a new Communication that will be sent to a random player, and has a random communication type.
     */
    Communication message_action(ArrayList<Integer> ids){
        if(rand.nextFloat() > this.comm_thresh){
            int num_coms = CommType.values().length;
            CommType comm_type = CommType.values()[rand.nextInt(num_coms)];
            int receiver = ids.get(rand.nextInt(ids.size()));

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

    //Medium player does not update policy so this function does nothing
    void update_policy(ArrayList<Integer> ids){}
}


