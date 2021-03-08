package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String message="";
        if(entity.getId()==null)
            message+="Invalid id! ";
        if(entity.getFirstName().equals(""))
            message+="Invalid first name! ";
        if(entity.getLastName().equals(""))
            message+="Invalid last name! ";
        if(!message.equals(""))
            throw new ValidationException(message);

    }
}
