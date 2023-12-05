package org.map.socialnetwork.validator;

import org.map.socialnetwork.domain.Message;
import org.map.socialnetwork.exception.ValidatorException;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidatorException {
        //TODO IMPLEMENT;
        if(entity == null)
            throw new ValidatorException("Entity is null");
    }
}
