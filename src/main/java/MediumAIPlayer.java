import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * Basic AI player class that corresponds to the Medium difficulty.
 * This player has deterministic behavior and needs to be trained by the AI handler.
 * Trains using Naive Bayes
 */
public class MediumAIPlayer extends AIPlayer{
    NaiveBayesUpdateable base_model;
    HashMap<Integer, Classifier> models;
    private float comm_thresh = 0.9f;
    private Random rand = new Random();

    MediumAIPlayer(int id, String name, ArrayList<Integer> ids, NaiveBayesUpdateable model){
        super(id, name, ids);
        this.base_model = model;
        this.models = new HashMap<Integer, Classifier>();

        // Assign a model for each other player
        for(int enemy_id: this.enemy_ids) {
            try {
                models.put(enemy_id, NaiveBayesUpdateable.makeCopy(this.base_model));
            }
            catch (Exception e){
                System.out.println("Exception in MediumAIPlayer model creation " + e);
            }
        }
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
     * Makes a instance from messages for use by model
     */
    Instance getInstance(int round_id, int enemy_id){
       DenseInstance instance = new DenseInstance(6);
       for(Communication comm: this.rcv_comms.get(round_id).get(enemy_id)){
           int comm_counts = 5;
       }
        //instance.setValue(key, comm_counts);
        return instance;
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

    void update_policy(){}
}


