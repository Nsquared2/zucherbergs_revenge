//TODO: @Vincent: Verify we don't need new CommType class and can just use ActionType for both action and comm? See Communication implementation. Do we need blank?

/**
 * This enum represents the six different pre-determined messages that players can send to each other.
 * Each CommType has a name to be exchanged between the server and client, and a full
 * message text that will be displayed to the user receiving the communication.
 */
public enum CommType{
    REQUEST_COOP("request_cooperate", "Request to cooperate"),
    REQUEST_BETRAY("request_betray", "Request to betray"),
    REQUEST_IGNORE("request_ignore", "Request to ignore"),
    PROMISE_COOP("announce_cooperate", "Promise to cooperate"),
    PROMISE_BETRAY("announce_betray", "Promise to betray"),
    PROMISE_IGNORE("announce_ignore", "Promise to ignore");

    private String name;
    private String message;

    CommType(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String toString() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
