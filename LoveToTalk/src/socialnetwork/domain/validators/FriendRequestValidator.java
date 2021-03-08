package socialnetwork.domain.validators;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Friendship;

public class FriendRequestValidator implements Validator<FriendRequest> {
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        String message="";
        if(entity.getId()==null)
            message+="Invalid id! ";
        if(entity.getStatus()==null)
            message+="Invalid status! ";
        if(!entity.getStatus().equals("pending") && !entity.getStatus().equals("approved") && !entity.getStatus().equals("rejected"))
            message+="Status must be pending/approved/rejected! ";
        if(!message.equals(""))
            throw new ValidationException(message);

    }
}