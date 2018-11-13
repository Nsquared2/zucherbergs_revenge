import java.util.ArrayList;

/**
 * Keeps track of player state as observed by the AI. Will be used for training.
 */
public class PlayerState {
    ArrayList<float[]> actions;
    ArrayList<float[]> comms;
}
