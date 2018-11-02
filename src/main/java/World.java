/**
 * Represents the world state for AI training, with the number of possible communications and actions
 */
public class World {
    public static int NUM_COMMS = 6;
    public static int NUM_ACTIONS = 3;

    static int num_actions(){
        return NUM_ACTIONS;
    }

    static int num_comms(){
        return NUM_COMMS;
    }
}
