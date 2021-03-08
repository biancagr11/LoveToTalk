package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long,Long>, Friendship> {
    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    public Friendship save(Friendship entity) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(entity.getId().getRight(),entity.getId().getLeft());
        Friendship p=findOne(compl);
        if(p!=null)
            throw new ValidationException("Id already exists!");
        return super.save(entity);
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> longLongTuple) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(longLongTuple.getRight(),longLongTuple.getLeft());
        Friendship friendship=super.findOne(longLongTuple);
        if(friendship==null)
            return super.findOne(compl);
        else
            return friendship;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> longLongTuple) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(longLongTuple.getRight(),longLongTuple.getLeft());
        Friendship p=findOne(compl);
        if(p!=null)
            longLongTuple=compl;
        return super.delete(longLongTuple);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        Friendship friendship=new Friendship(LocalDateTime.parse(attributes.get(2)));
        Tuple<Long,Long> idFriendship=new Tuple<Long,Long>(Long.parseLong(attributes.get(0)),
                Long.parseLong(attributes.get(1)));
        friendship.setId(idFriendship);
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        Tuple<Long,Long> idFriendship= entity.getId();
        return idFriendship.getLeft()+";"+ idFriendship.getRight()+";"+entity.getDate();
    }
}
