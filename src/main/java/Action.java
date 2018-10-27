public class Action extends Message{
    ActionType action;

    Action(String action_name){
        //TODO: Should user just pass in an ActionType instead of string?
        if(action_name.equals("cooperate")){
            this.action = ActionType.COOPERATE;
        }
        else if(action_name.equals("betray")){
            this.action = ActionType.BETRAY;
        }
        else{
            throw new IllegalArgumentException("Not a valid action type");
        }
    }

}
