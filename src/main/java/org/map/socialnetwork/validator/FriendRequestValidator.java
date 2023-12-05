package org.map.socialnetwork.validator;

import org.map.socialnetwork.domain.FriendRequest;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.exception.ValidatorException;

import java.util.Objects;

public class FriendRequestValidator implements Validator<FriendRequest> {

    private final UserValidator userValidator = new UserValidator();

    @Override
    public void validate(FriendRequest entity) throws ValidatorException {
        User firstUser = entity.getFirstUser();
        User secondUser = entity.getSecondUser();
        userValidator.validate(firstUser);
        userValidator.validate(secondUser);

        if(Objects.equals(firstUser.getID(), secondUser.getID()))
            throw new ValidatorException("Users IDs are the same!");

        if(!Objects.equals(entity.getStatus(), "pending") &&
                !Objects.equals(entity.getStatus(), "approved") &&
                !Objects.equals(entity.getStatus(), "rejected"))
            throw new ValidatorException("Wrong FriendRequest status!");

    }
}
