import java.util.ArrayList;
import java.util.Random;

public class EasyAIPlayer extends AIPlayer{
    Random rand = new Random();
    float comm_thresh = 0.9;

    EasyAIPlayer(int id, String name){
        super(id, name);
    }

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

    abstract Action round_action(ArrayList<Integer> ids) {
        int num_coms = CommType.values().length;

        for(int reciever: ids) {
            ActionType action_type = ActionType.values()[rand.nextInt(num_coms)];

            Action comm = new Action(action_type, this.id, reciever);
            return comm;
        }

    }

    //Easy player does not update policy so this function does nothing
    void update_policy(){}
}
