/**
 * Interface for all messages to go through server
 */
public abstract class Message extends Object{
    int sender_id;
    int reciever_id;
    //TODO: Do we want to reserve id 0 for message sent only to server? Will this ever happen?


    //TODO: Should we have a parse_input method here to check validity of string being used to create message?
    //abstract void parse_input(String input_string)

    void set_sender(int sender_id){
        this.sender_id = sender_id;
    }

    void set_reciever(int reciever_id){
        this.reciever_id = reciever_id;
    }

    int get_sender(){
        return this.sender_id;
    }

    int get_reciever(){
        return this.reciever_id;
    }
}
