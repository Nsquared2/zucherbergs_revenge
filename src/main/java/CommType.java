//TODO: @Vincent: Verify we don't need new CommType class and can just use ActionType for both action and comm? See Communication implementation. Do we need blank?

public enum CommType{
    REQUEST_COOP("request_cooperate", "You have been requested to select cooperate"),
    REQUEST_BETRAY("request_betray", "You have been requested to select betray"),
    REQUEST_IGNORE("request_ignore", "You have been requested to select ignore"),
    PROMISE_COOP("promise_cooperate", "You have received a promise for cooperation"),
    PROMISE_BETRAY("promise_betray", "You have received a promise for betrayal"),
    PROMISE_IGNORE("promise_ignore", "You have received a promise for ignore");

    private String stringRepresentation;
    private String message;

    CommType(String rep, String message) {
        this.stringRepresentation = rep;
        this.message = message;
    }

    public String toString() {
        return stringRepresentation;
    }

    public String getMessage() {
        return message;
    }
}
