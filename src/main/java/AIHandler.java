import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.SGD;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is responsible for maintaining the currently active AI players, creating new AI players,
 * and training the different difficulty levels.
 */
public class AIHandler {
    Instances data_format;

    static String model_path = "./models/";
    static String medium_model_path = model_path + "medium.model";
    static String hard_model_path = model_path + "hard.model";
    static String data_path = "./data/";
    static String data_file_name_prefix = "game";

    AIHandler() {
        Instances dataset = WekaData.makeDataset();
    }

    // ************ Game Start Interface **********************
    /**
     * Creates a new AI player based on the input parameters.
     * @param difficulty Selects a difficulty from 'easy', 'medium', or 'hard'
     * @param id The ID to be assigned to the AI player
     * @param name The name that will be displayed for this AI player
     * @param ids The list of ids of all game players
     * @return The new AI player, with the given ID and name, which will perform to the selected difficulty level
     */
    public AIPlayer createAi(String difficulty, int id, String name, ArrayList<Integer> ids){
        AIPlayer ai_player;

        if(difficulty.equals("easy")){
            ai_player = new EasyAIPlayer(id, name, ids);
        }
        else if(difficulty.equals("medium")){
            NaiveBayesUpdateable model = new NaiveBayesUpdateable();
            this.initializeModel(model, medium_model_path);
            ai_player = new TrainableAIPlayer(id, name, ids, model);
        }
        else if(difficulty.equals("hard")){
            SGD model = new SGD();
            model.setLossFunction(new SelectedTag(SGD.LOGLOSS, SGD.TAGS_SELECTION));
            this.initializeModel(model, hard_model_path);
            ai_player = new TrainableAIPlayer(id, name, ids, model);
        }
        else{
            throw new IllegalArgumentException(difficulty + " is not a valid difficulty string");
        }

        return ai_player;
    }


    // ************ Game End Interface **********************
    /**
     * This method handles the training procedures for the medium and hard policies.
     * The easy difficulty does not require training, as the behavior is deterministic.
     */
    void trainBasePolicies(){
        //easy policy is stochastic so never trained

        //medium policy training procedure
        NaiveBayesUpdateable medium_policy_model = new NaiveBayesUpdateable();
        trainClassifierWithGameData(medium_policy_model);
        saveBaseModel(medium_model_path, medium_policy_model);

        //hard policy training procedure
        SGD hard_policy_model = new SGD();
        hard_policy_model.setLossFunction(new SelectedTag(SGD.LOGLOSS, SGD.TAGS_SELECTION));
        trainClassifierWithGameData(hard_policy_model);
        saveBaseModel(hard_model_path, hard_policy_model);
    };


    /**
     * Adds the round observations of all the AI players to the database for future base policy training
     * @param players Arraylist of all AI players that participated in the game
     */
    void saveAiPlayerObservations(ArrayList<AIPlayer> players) {
        Instances dataset = WekaData.makeDataset();
        for (AIPlayer player : players) {
            //TODO: Append to ARFF file
            dataset = Instances.mergeInstances(dataset, player.round_instances);
        }

        int save_id = getSaveGameId();
        try {
            ConverterUtils.DataSink.write(data_file_name_prefix + Integer.toString(save_id) + ".arff", dataset);
        }
        catch (Exception e){
            System.out.println("Exception: Game data not saved " + e.toString());
        }
    }

    // ************ Utility **********************
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

    private <C extends Classifier> void trainClassifierWithGameData(C model){
        File data_dir = new File(data_path);
        ArrayList<Integer> game_ids = new ArrayList<Integer>();

        Instances dataset = WekaData.makeDataset();
        for(File file: data_dir.listFiles()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file.getName()));
                ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
                Instances game_data = arff.getData();
                game_data.setClassIndex(game_data.numAttributes() - 1);
                //TODO: Avoid loading everything into RAM?
                dataset = Instances.mergeInstances(dataset, game_data);
            }
            catch (Exception e){
                System.out.println("Error: File " + file.getName() + " not used by training.");
            }
        }

        System.out.println("Training base model...");
        try {
            model.buildClassifier(dataset);
        }
        catch (Exception e){
            System.out.println("Exception in training base policies: " + e.toString());
        }
    }

    /**
     * Saves a base policy to the model directory
     * @param path the path to the save the model
     * @param model the trained model object itself
     * @param <C> Classifier type
     */
    private <C extends Classifier> void saveBaseModel(String path, C model){
        try {
            weka.core.SerializationHelper.write(path, model);
        }
        catch (Exception e){
            System.out.println("Error in saving model " + path + " due to " + e.toString());
        }
    }

    /**
     * Used to get id for game being currently saved
     * @return (id of last saved game) + 1
     */
    private int getSaveGameId(){
        File data_dir = new File(data_path);
        ArrayList<Integer> game_ids = new ArrayList<Integer>();
        for(File file: data_dir.listFiles()){
            String name = file.getName();
            try {
                int id = Integer.valueOf(name.charAt(name.length() - 1));
                game_ids.add(id);
            }
            catch (NumberFormatException e){}
        }

        return Collections.max(game_ids)+1;
    }
}
