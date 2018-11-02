/**
 * This enum holds the different types of actions that a player can take against an adversary.
 */
public enum ActionType{
    COOPERATE("cooperate"),
    BETRAY("betray"),
    IGNORE("ignore");

    // This representation is meant to give a clear description of the action being performed
    private String stringRepresentation;

    ActionType(String rep) {
        this.stringRepresentation = rep;
    }

    public String toString() {
        return stringRepresentation;
    }
}
