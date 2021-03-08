package socialnetwork.repository.file;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MessageFile extends AbstractFileRepository<Long, ReplyMessage>{
    private Repository<Long, User> userFile;
    private Random random=new Random();
    public MessageFile(String fileName, Validator<ReplyMessage> validator,Repository<Long, User> userFile){
        this.userFile=userFile;
        super.validator = validator;
        super.entities=new HashMap<Long,ReplyMessage>();
        this.fileName=fileName;
        super.loadData();
    }

    @Override
    public ReplyMessage extractEntity(List<String> attributes){
        this.userFile=userFile;
        Long id=Long.parseLong(attributes.get(0));
        User from=userFile.findOne(Long.parseLong(attributes.get(1)));
        Integer nrTo=Integer.parseInt(attributes.get(2));
        List<User> to=new ArrayList<User>();
        int i=0;
        for(i=0;i<nrTo;i++){
            to.add(userFile.findOne(Long.parseLong(attributes.get(i+3))));
        }
        String text=attributes.get(i+3);
        LocalDateTime date= LocalDateTime.parse(attributes.get(i+4));
        ReplyMessage message;
        if(attributes.get(i+5).equals("null")){
            message = new ReplyMessage(from, to, text, date,null);
            message.setId(id);
        }
        else{
            Message toReply = findOne(Long.parseLong(attributes.get(i+5)));
            message = new ReplyMessage(from, to, text, date,toReply);
            message.setId(id);
        }
        return message;
    }

    @Override
    public ReplyMessage save(ReplyMessage entity) {
        Long id= random.nextLong()%1000;
        if(id<0);
            id=id*(-1);
        while(findOne(id)!=null){
            id= random.nextLong();
            if(id<0);
                id=id*(-1);
        }
        entity.setId(id);
        return super.save(entity);
    }

    @Override
    protected String createEntityAsString(ReplyMessage message){
        String to="";
        for(User user: message.getTo()){
            to=to+user.getId()+";";
        }
        if(message.getMessageToReply()!=null)
            return message.getId()+";"+message.getFrom().getId()+";"+message.getTo().size()+";"+
                to+message.getMessage()+";"+message.getDate()+";"+message.getMessageToReply().getId();
        else
            return message.getId()+";"+message.getFrom().getId()+";"+message.getTo().size()+";"+
                    to+message.getMessage()+";"+message.getDate()+";null";
    }
}
