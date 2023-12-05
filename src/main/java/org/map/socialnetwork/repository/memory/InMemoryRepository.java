package org.map.socialnetwork.repository.memory;

import org.map.socialnetwork.domain.Entity;
import org.map.socialnetwork.exception.RepositoryException;
import org.map.socialnetwork.exception.ValidatorException;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.validator.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID> > implements Repository<ID,E> {

    private final Map<ID,E> users;
    private final Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        users = new HashMap<>();
        this.validator = validator;
    }


    @Override
    public Optional<E> save(E entity) throws IllegalArgumentException, ValidatorException, RepositoryException{
        if (entity == null)
            throw new IllegalArgumentException("Invalid Entity");

        validator.validate(entity);

        if(users.get(entity.getID()) != null ) {
            throw new RepositoryException("Existent ID");
        }

        //If result si null (key not already associated with a value) return the entity
        //else return null
        return users.putIfAbsent(entity.getID(),entity) == null ?
                Optional.of(entity) :
                Optional.empty();
    }

    @Override
    public Optional<E> findOne(ID id) throws RepositoryException{
        if(users.get(id) == null)
            throw new RepositoryException("Nonexistent ID");

        return Optional.of(users.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return users.values();
    }

    @Override
    public Optional<E> update(E entity) throws IllegalArgumentException, ValidatorException, RepositoryException{
        if (entity == null)
            throw new IllegalArgumentException("Invalid Entity");

        validator.validate(entity);

        if(users.get(entity.getID()) == null) {
            throw new RepositoryException("Nonexistent ID");
        }

        return Optional.ofNullable(users.replace(entity.getID(),entity));
    }

    @Override
    public Optional<E> delete(ID id) throws RepositoryException{
        if(users.get(id) == null) {
            throw new RepositoryException("Nonexistent ID");
        }
        return Optional.ofNullable(users.remove(id));
    }
}
