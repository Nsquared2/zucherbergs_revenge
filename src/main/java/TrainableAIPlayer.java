import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * Basic AI player class that corresponds to the Medium difficulty.
 * This player has deterministic behavior and needs to be trained by the AI handler.
 * Trains using Naive Bayes
 */
public class TrainableAIPlayer<C extends UpdateableClassifier & Classifier> extends AIPlayer{
    C base_model;
    HashMap<Integer, C> models;
    private float comm_thresh = 0.9f;
    private Random rand = new Random();

    TrainableAIPlayer(int id, String name, ArrayList<Integer> ids, C model){
        super(id, name, ids);
        this.base_model = model;
        this.models = new HashMap<Integer, C>();

        // Assign a model for each other player
        for(int enemy_id: this.enemy_ids) {
            try {
                models.put(enemy_id, (C) AbstractClassifier.makeCopy(this.base_model));
            }
            catch (Exception e){
                System.out.println("Exception in TrainableAIPlayer model creation " + e);
            }
        }
    }

    /**
     * Returns a new Communication that will be sent to a random player, and has a random communication type.
     */
    @Override
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
     * Creates an Action for each of the Player IDs given as input using C model
     */
    @Override
    ArrayList<Action> round_action() {
        int num_actions = ActionType.values().length;
        ArrayList<Action> actions = new ArrayList<Action>(0);
        int round_id = rcv_comms.size()-1;

        for(int enemy: this.enemy_ids) {
            DenseInstance instance = WekaData.makeInstance(rcv_comms.get(round_id).get(enemy));
            double[] distribution;
            try {
                distribution = this.models.get(enemy_ids).distributionForInstance(instance);
            }
            catch (Exception e){
                System.out.println("Error in TrainableAIPlayer model evaluation " + e.toString());
                distribution = new double[World.num_actions()];
            }

            //TODO: Take into account prior
            int max_action = Util.argmax(distribution);
            ActionType enemy_action = Util.enumIndexToValue(ActionType.class, max_action);
            ActionType my_action = this.maximizeValue(enemy_action);
            Action action = new Action(my_action, this.id, enemy);
            actions.add(action);
        }

        return actions;
    }

    @Override
    void update_policy(ArrayList<HashMap<Integer, ActionType>> round_results){
        int round_id = rcv_comms.size()-1;
        for(int key: this.enemy_ids){
            //Make instance from round data
            C model = this.models.get(key);
            Collection<Communication> comms = this.rcv_comms.get(round_id).get(key);
            DenseInstance instance = WekaData.makeInstance(comms);

            //Update round history
            this.round_instances.add(instance);

            //Update classifier
            try{ model.updateClassifier(instance);}
            catch (Exception e) {
                System.out.println("Exception in trainable AI update " + e.toString());
                System.exit(1);
            }
        }

        //Add new layer to rcv comms for next round
        addMapLayer();
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
}


