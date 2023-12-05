package org.map.socialnetwork.service;

import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.exception.RepositoryException;
import org.map.socialnetwork.exception.ValidatorException;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.utils.Events.EventType;
import org.map.socialnetwork.utils.Events.UserEvent;
import org.map.socialnetwork.utils.Events.UserFriendshipEvent;
import org.map.socialnetwork.utils.Observer.Observable;
import org.map.socialnetwork.utils.Observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService implements Observable{

    Repository<Long, User> Repository;

    List<Observer> observerList = new ArrayList<>();

    public UserService(Repository<Long, User> Repository) {
        this.Repository = Repository;
    }

    public Optional<User> saveToRepository(String firstName, String secondName) throws IllegalArgumentException, ValidatorException, RepositoryException{
        Optional<User> result =  Repository.save( new User(firstName,secondName) );
        result.ifPresent( user -> notifyObservers());
        return result;
    }

    public Optional<User> getFromRepository(Long id) throws RepositoryException{
        return Repository.findOne(id);
    }

    public Iterable<User> getAll() {
        return Repository.findAll();
    }

    public Optional<User> updateToRepository(Long id, String firstName, String lastName) throws IllegalArgumentException, ValidatorException, RepositoryException {
        Optional<User> optionalUser = Repository.findOne(id);

        optionalUser.ifPresent( user -> {
            User oldUser = new User(user.getID(), user.getFirstName(), user.getLastName());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            Repository.update(user);
            notifyObservers();
        });

        return optionalUser;
    }

    public Optional<User> removeFromRepository(Long id) throws RepositoryException {

        Optional<User> result = Repository.delete(id);
        result.ifPresent(user -> notifyObservers());
        return result;

    }

    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observerList.forEach(observer -> observer.update());
    }
}
