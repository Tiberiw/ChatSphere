package org.map.socialnetwork.service;

import org.map.socialnetwork.domain.*;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.repository.paging.PagingRepository;
import org.map.socialnetwork.utils.Observer.Observable;
import org.map.socialnetwork.utils.Observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestService implements Observable {

    PagingRepository<Long, FriendRequest> friendRequestRepository;
    PagingRepository<Pair<Long,Long>, Friendship> friendshipRepository;
    PagingRepository<Long, User> userRepository;

    List<Observer> observerList = new ArrayList<>();
    public FriendRequestService(PagingRepository<Long, FriendRequest> friendRequestRepository, PagingRepository<Pair<Long, Long>, Friendship> friendshipRepository, PagingRepository<Long, User> userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public void sendFriendRequest(User fromUser, User toUser) {
        FriendRequest friendRequest = new FriendRequest(fromUser, toUser, "pending");

        friendRequestRepository.save(friendRequest);
        notifyObservers();
    }

    public void approveFriendRequest(FriendRequest friendRequest) {
        friendRequest.setStatus("approved");
        //TODO ORDER MATTERS

        Friendship newFriendship = friendRequest.getFirstUser().getID() < friendRequest.getSecondUser().getID() ?
                new Friendship(friendRequest.getFirstUser(), friendRequest.getSecondUser(), LocalDateTime.now()) :
                new Friendship(friendRequest.getSecondUser(), friendRequest.getFirstUser(), LocalDateTime.now());

        friendshipRepository.save(newFriendship);
        friendRequestRepository.update(friendRequest);
        notifyObservers();

    }

    public void rejectFriendRequest(FriendRequest friendRequest) {
        friendRequest.setStatus("rejected");
        friendRequestRepository.update(friendRequest);
        notifyObservers();

    }

    public List<FriendRequest> getFriendRequestsReceivedBy(User user) {

       return StreamSupport.stream(friendRequestRepository.findAll().spliterator(), false)
                    .filter( fr -> Objects.equals(fr.getSecondUser().getID(), user.getID()))
                    .toList();


    }



    public List<FriendRequest> getFriendRequestsSentBy(User user) {

        return StreamSupport.stream(friendRequestRepository.findAll().spliterator(), false)
                .filter( fr -> Objects.equals(fr.getFirstUser().getID(), user.getID()))
                .toList();


    }

    public List<User> getRequestSenders(User user) {
        return getFriendRequestsReceivedBy(user).stream()
                .filter(fr -> fr.getStatus().equals("pending"))
                .map(FriendRequest::getFirstUser)
                .toList();
    }


    public List<FriendRequest> getPendingRequests(User user) {
        return getFriendRequestsReceivedBy(user).stream()
                .filter(fr -> fr.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }


    public List<User> getPossibleFriends(User user) {

        List<User> friendsOfUser = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter( r -> Objects.equals(r.getFirstUser().getID(), user.getID()) || Objects.equals(r.getSecondUser().getID(), user.getID()))
                .map( fr -> Objects.equals(user.getID(), fr.getFirstUser().getID()) ? fr.getSecondUser()  : fr.getFirstUser())
                .toList();


        List<User> pendingSenders = getFriendRequestsReceivedBy(user).stream()
                .filter(fr -> fr.getStatus().equals("pending"))
                .map(FriendRequest::getFirstUser)
                .toList();

        List<User> pendingReceivers = getFriendRequestsSentBy(user).stream()
                .filter(fr -> fr.getStatus().equals("pending"))
                .map(FriendRequest::getSecondUser)
                .toList();

        List<User> allUsers = new ArrayList<>(StreamSupport.stream(userRepository.findAll().spliterator(), false).toList());
        allUsers.remove(user);

        return allUsers.stream().filter( usr -> !friendsOfUser.contains(usr))
                .filter(usr -> !pendingSenders.contains(usr))
                .filter(usr -> !pendingReceivers.contains(usr))
                .toList();

    }

    public void setPageSizeFriendRequest(int pageSizeFriendRequest) {
        friendRequestRepository.setPageSize(pageSizeFriendRequest);
    }

    public void setPageNumberFriendRequest(int pageNumberFriendRequest) {
        friendRequestRepository.setPageNumber(pageNumberFriendRequest);
    }

    public int getPageNumberFriendRequest() {
        return friendRequestRepository.getPageNumber();
    }

    public int getNumberOfPagesFriendRequest() {
        return friendRequestRepository.getNumberOfPages();
    }

    public void setPageSizeFriendships(int pageSizeFriendships) {
        friendshipRepository.setPageSize(pageSizeFriendships);
    }

    public void setPageNumberFriendships(int pageNumberFriendships) {
        friendshipRepository.setPageNumber(pageNumberFriendships);
    }

    int getNumberOfPagesFriendships() {
        return friendshipRepository.getNumberOfPages();
    }

    public void setPageSizeUsers(int pageSizeUsers) {
        userRepository.setPageSize(pageSizeUsers);
    }

    public void setPageNumberUsers(int pageNumberUsers) {
        userRepository.setPageNumber(pageNumberUsers);
    }

    int getNumberOfPagesUsers() {
        return userRepository.getNumberOfPages();
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
        observerList.forEach(Observer::update);
    }
}
