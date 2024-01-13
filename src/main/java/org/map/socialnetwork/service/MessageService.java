package org.map.socialnetwork.service;

import org.map.socialnetwork.domain.Entity;
import org.map.socialnetwork.domain.Message;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.utils.Observer.Observable;
import org.map.socialnetwork.utils.Observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

public class MessageService implements Observable {

    Repository<Long, Message> messageRepository;

    List<Observer> observerList = new ArrayList<>();


    public MessageService(Repository<Long, Message> messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getChronologicalConv(User firstUser, User secondUser) {
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .filter(message -> ((Objects.equals(message.getSender().getID(), firstUser.getID()) && message.getReceivers().contains(secondUser)) || (Objects.equals(message.getSender().getID(), secondUser.getID()) && message.getReceivers().contains(firstUser) )))
                .sorted(Comparator.comparing(Entity::getID))
                .toList();
    }

    public Optional<Message> sendMessage(User fromUser, List<User> toUsers, String message, Message reply) {
        Optional<Message> result =  messageRepository.save(new Message(fromUser,toUsers,message, LocalDateTime.now(),reply));
        notifyObservers();
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
        observerList.forEach( observer -> observer.update());
    }
}
