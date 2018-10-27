import org.apache.spark.ml.classification.LogisticRegression;

public class AIHandler {
    AIHandler(){};

    AIPlayer create_ai(String difficulty, int id, String name){
        AIPlayer ai_player;

        if(difficulty.equals("easy")){
            ai_player = new EasyAIPlayer(id, name);
        }
        else if(difficulty.equals("medium")){

        }
        else if(difficulty.equals("hard")){

        }
        else{
            throw new IllegalArgumentException(difficulty + " is not a valid difficulty string");
        }

        return ai_player;
    };

    void train_base_policies(){
        //easy policy is deterministic so never trained

        //medium policy training procedure

        //hard policy training procedure
    };


}
