package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message{
    Message messageToReply;

    public ReplyMessage(){}
    public ReplyMessage(User from, List<User> to, String message, LocalDateTime date, Message messageToReply){
        super(from,to,message,date);
        this.messageToReply=messageToReply;
    }

    public Message getMessageToReply() { return messageToReply; }

}
