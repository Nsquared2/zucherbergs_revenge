/**
 * This enum holds the different types of actions that a player can take against an adversary.
 */
public enum ActionType{
    COOPERATE("cooperate", "Your opponent cooperated!", "You cooperated with your opponent!"),
    BETRAY("betray", "Your opponent betrayed you!", "You betrayed your opponent!"),
    IGNORE("ignore", "Your opponent ignored you!", "You ignored your opponent!");

    // This representation is meant to give a clear description of the action being performed
    private String stringRepresentation;
    private String message;
    private String performerMessage;

    ActionType(String rep, String m, String perform) {
        this.stringRepresentation = rep;
        this.message = m;
        this.performerMessage = perform;
    }


    public String toString() {
        return stringRepresentation;
    }

    public String getMessage() {
        return message;
    }

    public String getPerformerMessage() {
        return performerMessage;
    }
}
