public class Action extends Message{
    ActionType action;

    Action(ActionType actionType){
        this.action = actionType;
    }

    Action(ActionType actionType, int sender_id){
        this.action = actionType;
        this.sender_id = sender_id;
    }

    Action(ActionType actionType, int senderId, int reciever_id){
        this.action = actionType;
        this.sender_id = sender_id;
        this.reciever_id = reciever_id;
    }

    public String toString() {return action.toString();}
}
