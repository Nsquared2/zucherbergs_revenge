public class Action extends Message{
    ActionType action;

    Action(ActionType actionType){
        this.action = actionType;
    }

    public String toString() {
        return action.toString();
    }
}
