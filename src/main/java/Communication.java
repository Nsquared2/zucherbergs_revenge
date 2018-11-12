//TODO: Vincent maybe you can fill this out? Use CommTypes enum

/**
 * This class represents a communication sent between two players.
 * A communication contains the CommType and IDs of the sender and receiver.
 */
public class Communication extends Message{
//    CommType you_do;
//    CommType i_do;

//    Communication(CommType you_do, CommType i_do, int sender_id){
//        this.you_do = you_do;
//        this.i_do = i_do;
//        this.sender_id = sender_id;
//    }

    private CommType action;

    Communication(CommType action, int senderId, int reciever_id){
        this.action = action;
        this.sender_id = sender_id;
        this.reciever_id = reciever_id;
    }

    CommType getAction(){
        return this.action;
    }

//    public String toString() {
//        return this.you_do.toString() + " " + this.i_do.toString();
//    }

    public String toString() {
        return this.action.toString();
    }
}
