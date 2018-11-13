/**
 * This class represents an Action taken on behalf of a given player, towards a recipient player.
 * Actions can come from a set of ActionTypes, and store the IDs of the sending/receiving players.
 */

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

    /**
     * Delegates the toString call to the ActionType enum to get a meaningful desc. of the action
     */
    public String toString() {return action.toString();}
}
