package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String message="";
        if(entity.getId()==null)
            message+="Invalid id! ";
        if(entity.getId().getLeft()==null)
            message+="Invalid left friend! ";
        if(entity.getId().getRight()==null)
            message+="Invalid right friend! ";
        if(entity.getId().getRight()==entity.getId().getLeft())
            message+="Frineds must be different! ";
        if(!message.equals(""))
            throw new ValidationException(message);

    }
}
