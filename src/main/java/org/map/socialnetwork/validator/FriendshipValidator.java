package org.map.socialnetwork.validator;

import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.exception.ValidatorException;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {

    private final UserValidator userValidator = new UserValidator();
    @Override
    public void validate(Friendship entity) throws ValidatorException {
        User firstUser = entity.getFirstUser();
        User secondUser = entity.getSecondUser();
        userValidator.validate(firstUser);
        userValidator.validate(secondUser);
        
        if(Objects.equals(firstUser.getID(), secondUser.getID()))
            throw new ValidatorException("Users IDs are the same!");
    }
}
