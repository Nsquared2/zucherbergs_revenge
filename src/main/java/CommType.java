//TODO: @Vincent: Verify we don't need new CommType class and can just use ActionType for both action and comm? See Communication implementation. Do we need blank?

/**
 * This enum represents the six different pre-determined messages that players can send to each other.
 * Each CommType has a name to be exchanged between the server and client, and a full
 * message text that will be displayed to the user receiving the communication.
 */
public enum CommType{
    REQUEST_COOP("request_cooperate", "You have been requested to select cooperate"),
    REQUEST_BETRAY("request_betray", "You have been requested to select betray"),
    REQUEST_IGNORE("request_ignore", "You have been requested to select ignore"),
    PROMISE_COOP("declare_cooperate", "You have received a promise for cooperation"),
    PROMISE_BETRAY("declare_betray", "You have received a promise for betrayal"),
    PROMISE_IGNORE("declare_ignore", "You have received a promise for ignore");

    private String name;
    private String message;

    CommType(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public static int getIndex(String CommType){
        if(CommType == REQUEST_COOP)
            return 0
    }

    public String toString() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
