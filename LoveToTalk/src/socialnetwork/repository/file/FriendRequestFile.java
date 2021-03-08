package socialnetwork.repository.file;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

public class FriendRequestFile extends AbstractFileRepository<Tuple<Long,Long>, FriendRequest>{
    public FriendRequestFile(String fileName, Validator<FriendRequest> validator) {
        super(fileName, validator);
    }
    @Override
    public FriendRequest save(FriendRequest entity) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(entity.getId().getRight(),entity.getId().getLeft());
        FriendRequest p=findOne(compl);
        if(p!=null)
            return p;
        return super.save(entity);
    }

    @Override
    public FriendRequest findOne(Tuple<Long, Long> longLongTuple) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(longLongTuple.getRight(),longLongTuple.getLeft());
        FriendRequest friendRequest=super.findOne(longLongTuple);
        if(friendRequest==null)
            return super.findOne(compl);
        else
            return friendRequest;
    }

    @Override
    public FriendRequest delete(Tuple<Long, Long> longLongTuple) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(longLongTuple.getRight(),longLongTuple.getLeft());
        FriendRequest p=findOne(compl);
        if(p!=null)
            longLongTuple=compl;
        return super.delete(longLongTuple);
    }

    @Override
    public FriendRequest extractEntity(List<String> attributes) {
        FriendRequest friendRequest=new FriendRequest(attributes.get(2));
        Tuple<Long,Long> idFriendRequest=new Tuple<Long,Long>(Long.parseLong(attributes.get(0)),
                Long.parseLong(attributes.get(1)));
        friendRequest.setId(idFriendRequest);
        return friendRequest;
    }

    @Override
    protected String createEntityAsString(FriendRequest entity) {
        Tuple<Long,Long> idFriendship= entity.getId();
        return idFriendship.getLeft()+";"+ idFriendship.getRight()+";"+entity.getStatus();
    }

    public void update(Tuple<Long,Long> id, String newStatus) {
        FriendRequest friendRequest=findOne(id);
        if(friendRequest.getId().getRight()!=id.getRight())
            throw new ValidationException("This friend request does not exist!");
        if(!friendRequest.getStatus().equals("pending"))
            throw new ValidationException("This friend request is not a pending request!");
        if(!newStatus.equals("approved") && !newStatus.equals("rejected"))
            throw new ValidationException("Invalid new status!");
        friendRequest.setStatus(newStatus);
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        for(FriendRequest entity:findAll()){
            writeToFile(entity);
        }
    }
}
