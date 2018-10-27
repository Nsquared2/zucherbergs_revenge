import java.util.ArrayList;

public abstract class AIPlayer {
    int id;
    String name;

    int score;
    ArrayList<ArrayList<String>> action_history;
    ArrayList<PlayerState> game_state;

    AIPlayer(int id, String name){
        this.id = id;
        this.name = name;
    }

    abstract String message_action();

    abstract Action round_action();

    abstract void update_policy();
}
