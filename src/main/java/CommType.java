//TODO: @Vincent: Verify we don't need new CommType class and can just use ActionType for both action and comm? See Communication implementation. Do we need blank?

public enum CommType{
    COOPERATE("cooperate"),
    BETRAY("betray"),
    IGNORE("ignore"),
    BLANK("blank");

    private String stringRepresentation;

    CommType(String rep) {
        this.stringRepresentation = rep;
    }

    public String toString() {
        return stringRepresentation;
    }
}
