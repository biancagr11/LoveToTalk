package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.repository.file.UserFile;

public class MessageValidator implements Validator<ReplyMessage>{

    @Override
    public void validate(ReplyMessage entity) throws ValidationException {
        String errors="";
        if(entity.getId()==null)
            errors+="Invalid id!\n";
        if(!errors.equals(""))
            throw new ValidationException(errors);
    }
}
