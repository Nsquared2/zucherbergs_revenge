//TODO: Vincent maybe you can fill this out? Use CommTypes enum

public class Communication extends Message{
    CommType you_do;
    CommType i_do;

    Communication(CommType you_do, CommType i_do, int sender_id){
        this.you_do = you_do;
        this.i_do = i_do;
        this.sender_id = sender_id;
    }

    Communication(CommType actionType, int senderId, int reciever_id){
        this.you_do = you_do;
        this.i_do = i_do;
        this.sender_id = sender_id;
        this.reciever_id = reciever_id;
    }

    public String toString() {
        return this.you_do.toString() + " " + this.i_do.toString();
    }
}
