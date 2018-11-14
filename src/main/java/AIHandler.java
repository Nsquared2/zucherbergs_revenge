//https://databricks.com/blog/2016/05/31/apache-spark-2-0-preview-machine-learning-model-persistence.html
//https://stackoverflow.com/questions/33556543/how-to-save-model-and-apply-it-on-a-test-dataset-on-java
//https://www.youtube.com/watch?v=wSB5oByt7ko

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * This class is responsible for maintaining the currently active AI players, creating new AI players,
 * and training the different difficulty levels.
 */
public class AIHandler {
    Instances data_format;
    String model_path;

    AIHandler(){
        Instances dataset = WekaData.makeDataset();
        model_path = "./models/";
    };

    /**
     * Creates a new AI player based on the input parameters.
     * @param difficulty Selects a difficulty from 'easy', 'medium', or 'hard'
     * @param id The ID to be assigned to the AI player
     * @param name The name that will be displayed for this AI player
     * @param ids The list of ids of all game players
     * @return The new AI player, with the given ID and name, which will perform to the selected difficulty level
     */
    public AIPlayer create_ai(String difficulty, int id, String name, ArrayList<Integer> ids){
        AIPlayer ai_player;

        if(difficulty.equals("easy")){
            ai_player = new EasyAIPlayer(id, name, ids);
        }
        else if(difficulty.equals("medium")){
            NaiveBayesUpdateable model = new NaiveBayesUpdateable();
            this.initializeModel(model, this.model_path+"medium.model");
            ai_player = new MediumAIPlayer(id, name, ids, model);
        }
        else if(difficulty.equals("hard")){
            // TODO: Hard ai
            ai_player = new EasyAIPlayer(id, name, ids);
        }
        else{
            throw new IllegalArgumentException(difficulty + " is not a valid difficulty string");
        }

        return ai_player;
    }

    /**
     * Initialize a model from saved data or create a new untrained model if none exists
     * @param model initialized model of classifier type C
     * @param path filepath to model
     * @param <C> Classifier type
     */
    private <C extends Classifier> void initializeModel(C model, String path){
        // check if model exists, if so load
        try {
            model = (C) weka.core.SerializationHelper.read(path);
        }
        catch (Exception e){
            //TODO: catch specific exception}
            try{ model.buildClassifier(this.data_format);}
            catch (Exception f){System.out.println("Exception in building classifier " + f.toString());}
        }
    }

    /**
     * This method handles the training procedures for the medium and hard policies.
     * The easy difficulty does not require training, as the behavior is deterministic.
     */
    void train_base_policies(){
        //easy policy is deterministic so never trained

        //medium policy training procedure

        //hard policy training procedure
    };


}
