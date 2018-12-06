import java.util.*;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.SparseInstance;

/**
 * Basic AI player class that corresponds to the Medium difficulty.
 * This player has deterministic behavior and needs to be trained by the AI handler.
 * Trains using Naive Bayes
 */
public class TrainableAIPlayer<C extends UpdateableClassifier & Classifier> extends AIPlayer{
    C base_model;
    HashMap<Integer, C> models;
    private float comm_thresh = 0.9f;

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
     * Creates an Action for each of the Player IDs given as input using C model
     */
    @Override
    HashMap<Integer, Action> round_action() {
        int num_actions = ActionType.values().length;
        HashMap<Integer, Action> actions = new HashMap<Integer, Action>();
        int round_id = rcv_comms.size()-1;

        for(int enemy: this.enemy_ids) {
            SparseInstance instance = WekaData.makeInstance(rcv_comms.get(round_id).get(enemy), this.eval_data);
            double[] distribution;
            double[] prior;
            try {
                distribution = this.models.get(enemy).distributionForInstance(instance);
                prior = this.base_model.distributionForInstance(instance);
            }
            catch (Exception e){
//                System.out.println("Error in TrainableAIPlayer model evaluation " + e.toString());
                distribution = new double[World.num_actions()];
                prior = new double[World.num_actions()];
            }

            //TODO: Take into account "prior"
//            double prior_weight = Math.exp(-round_id);
            double prior_weight = 0;
            double dist_weight = 1.0 - prior_weight;

            Util.scalarArrayMultiply(prior, prior_weight);
            Util.scalarArrayMultiply(distribution, dist_weight);
            Util.elementwiseAdd(distribution, prior);

            int max_action = Util.argmax(distribution);
            ActionType enemy_action = Util.enumIndexToValue(ActionType.class, max_action);
            ActionType my_action = this.maximizeValue(enemy_action);
            Action action = new Action(my_action, this.id, enemy);
            actions.put(enemy, action);
            currentRoundActions.put(enemy, my_action);
        }

        return actions;
    }

    @Override
    void update_policy(HashMap<Integer, ActionType> round_results){
        int round_id = rcv_comms.size()-1;
        for(int key: this.enemy_ids){
            //Make instance from round data
            C model = this.models.get(key);
            Collection<Communication> comms = this.rcv_comms.get(round_id).get(key);
            ActionType enemy_action = round_results.get(key);
            DenseInstance instance = WekaData.makeInstance(comms, enemy_action, this.round_instances);

            //Update round history
            this.round_instances.add(instance);
//            this.round_instances.lastInstance();
            //Update classifier
            try{
                model.updateClassifier(this.round_instances.lastInstance());
            }
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

    public C getBase_model(){
        return this.base_model;
    }
}


