package org.map.socialnetwork.service;

import org.map.socialnetwork.domain.Friend_DTO;
import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.Pair;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.exception.RepositoryException;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.utils.Events.UserFriendshipEvent;
import org.map.socialnetwork.utils.Graph;
import org.map.socialnetwork.utils.Observer.Observable;
import org.map.socialnetwork.utils.Observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipService implements Observable {

    Repository<Pair<Long,Long>, Friendship> friendshipRepository;
    Repository<Long,User> userRepository;
    Graph<User> Network;
    List<Observer> observerList = new ArrayList<>();
    public FriendshipService(Repository<Pair<Long,Long>, Friendship> friendshipRepository, Repository<Long,User> userRepository) {
        this.Network = new Graph<>();
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public Optional<Friendship> saveToRepository(Long el1, Long el2, String dateString) {

        //Find the 2 users
        Optional<User> optionalUser1 = userRepository.findOne(el1);
        Optional<User> optionalUser2 = userRepository.findOne(el2);


        LocalDateTime date = dateString.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(dateString);


        if(optionalUser1.isPresent() && optionalUser2.isPresent()) {
            Friendship newFriendship = el1 < el2 ?
                    new Friendship(optionalUser1.get(),optionalUser2.get(), date) :
                    new Friendship(optionalUser2.get(),optionalUser1.get(), date);

            Optional<Friendship> result = friendshipRepository.save(newFriendship);
            result.ifPresent(friendship -> notifyObservers());


            return result;
        }
        return Optional.empty();
    }

    public Optional<Friendship> getFromRepository(Long idFirstUser, Long idSecondUser) {
        return friendshipRepository.findOne(new Pair<>(idFirstUser,idSecondUser));
    }

    public Iterable<Friendship> getAll() {
        return friendshipRepository.findAll();
    }

    public Optional<User> getFromRepositoryUser(Long id) throws RepositoryException {
        return userRepository.findOne(id);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Friendship> updateToRepository(Long idFirstUser, Long idSecondUser, String stringDate) {

        Optional<User> firstUser = userRepository.findOne(idFirstUser);
        Optional<User> secondUser = userRepository.findOne(idSecondUser);
        Pair<Long,Long> id = idFirstUser < idSecondUser ?
                new Pair<>(idFirstUser,idSecondUser) :
                new Pair<>(idSecondUser,idFirstUser);
        LocalDateTime date = LocalDateTime.parse(stringDate);
        Optional<Friendship> oldFriendship = friendshipRepository.findOne(id);

        if(firstUser.isPresent() && secondUser.isPresent()) {
            Friendship newFriendship = new Friendship(id,firstUser.get(),secondUser.get(),date);

            Optional<Friendship> result = friendshipRepository.update(newFriendship);
            notifyObservers();
            return result;
        }
        return Optional.empty();
    }


    public Optional<Friendship> removeFromRepository(Long idFirstUser, Long idSecondUser) {

        Pair<Long,Long> id = idFirstUser < idSecondUser ?
                new Pair<>(idFirstUser,idSecondUser) :
                new Pair<>(idSecondUser,idFirstUser);

        Optional<Friendship> oldFriendship = friendshipRepository.findOne(id);

        Optional<Friendship> result = friendshipRepository.delete(id);
        //Remove friendship from repository
        notifyObservers();

        return result;

    }

    public void removeAllFriends(User user) {
        loadNetwork();

        //Remove all friendships of the user
        Network.getAllNeighbours(user)
                .forEach( e -> removeFromRepository(user.getID(),e.getID()));
    }


    public List<Friend_DTO> getAllFriendsUser(Long userId) {

        return StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                        .filter( r -> Objects.equals(r.getFirstUser().getID(), userId) || Objects.equals(r.getSecondUser().getID(), userId))
                        .map( fr -> Objects.equals(userId, fr.getFirstUser().getID()) ? new Friend_DTO(fr.getSecondUser(),fr.getDate())  : new Friend_DTO(fr.getFirstUser(),fr.getDate()))
                .collect(Collectors.toList());

    }

    public List<Friend_DTO> getAllFriendsUserMonth(Long userId, String month) {
        return getAllFriendsUser(userId).stream()
                .filter( dto -> dto.getDate().getMonth().toString().equals(month.toUpperCase()))
                .collect(Collectors.toList());
    }


    //Get all friend user ->data filter - in JAVA
    private void loadNetwork() {

        List<Friendship> allFriendships = new ArrayList<>();
        friendshipRepository.findAll().forEach(allFriendships::add);

        //Convert friendship object into a pair of Users
        var allEdges = allFriendships
                .stream()
                .map(friendship -> new Pair<>(friendship.getFirstUser(),friendship.getSecondUser()))
                .toList();

        //Get all users (nodes)
        List<User> allVertices = new ArrayList<>();
        userRepository.findAll().forEach(allVertices::add);

        //Set network adjacency list
        Network.setAdjacencyList(allEdges, allVertices);
    }

    public String showNetwork() {
        loadNetwork();
        return Network.toString();
    }

    public String getCommunities() {
        loadNetwork();
        return Network.showConnectedComponents();
    }

    public Map<Long, List<User>> getCommunitiesMap() {
        loadNetwork();
        return Network.getConnectedComponentsMap();
    }

    public Integer getCommunityNumber() {
        loadNetwork();
        return Network.getConnectedComponentsNumber();
    }

    public String getMostSocial() {
        loadNetwork();
        return Network.showMostSocial();
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
