package org.map.socialnetwork.validator;

import org.map.socialnetwork.exception.ValidatorException;

public interface Validator<T> {
        void validate(T entity) throws ValidatorException;
}
