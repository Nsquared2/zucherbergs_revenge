public enum ActionType{
    COOPERATE("cooperate"),
    BETRAY("betray"),
    IGNORE("ignore");

    private String stringRepresentation;

    ActionType(String rep) {
        this.stringRepresentation = rep;
    }

    public String toString() {
        return stringRepresentation;
    }
}


