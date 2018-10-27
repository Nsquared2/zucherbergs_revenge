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

    abstract Communication message_action();

    abstract Action round_action();

    abstract void update_policy();
}
