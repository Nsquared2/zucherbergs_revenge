import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public abstract class AIPlayer {
    int id;
    String name;

    int score;
    ArrayList<ArrayList<String>> action_history;
    Map<Integer, PlayerState> game_state = new HashMap<Integer, PlayerState>();

    AIPlayer(int id, String name){
        this.id = id;
        this.name = name;
    }

    abstract Communication message_action(ArrayList<Integer> ids);

    abstract ArrayList<Action> round_action(ArrayList<Integer> ids);

    abstract void update_policy(ArrayList<Integer> ids);
}
