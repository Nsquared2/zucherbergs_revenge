//import org.apache.spark.ml.classification.LogisticRegression;

/**
 * This class is responsible for maintaining the currently active AI players, creating new AI players,
 * and training the different difficulty levels.
 */
public class AIHandler {
    AIHandler(){};

    /**
     * Creates a new AI player based on the input parameters.
     * @param difficulty Selects a difficulty from 'easy', 'medium', or 'hard'
     * @param id The ID to be assigned to the AI player
     * @param name The name that will be displayed for this AI player
     * @return The new AI player, with the given ID and name, which will perform to the selected difficulty level
     */
    public static AIPlayer create_ai(String difficulty, int id, String name){
        AIPlayer ai_player;

        if(difficulty.equals("easy")){
            ai_player = new EasyAIPlayer(id, name);
        }
        else if(difficulty.equals("medium")){
            //TODO: Change these to other AI difficulties
            ai_player = new EasyAIPlayer(id, name);
        }
        else if(difficulty.equals("hard")){
            ai_player = new EasyAIPlayer(id, name);
        }
        else{
            throw new IllegalArgumentException(difficulty + " is not a valid difficulty string");
        }

        return ai_player;
    };

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
