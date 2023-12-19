package org.map.socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.map.socialnetwork.controller.StartController;
import org.map.socialnetwork.domain.*;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.repository.database.FriendRequestDatabaseRepository;
import org.map.socialnetwork.repository.database.FriendshipDatabaseRepository;
import org.map.socialnetwork.repository.database.MessageDatabaseRepository;
import org.map.socialnetwork.repository.database.UserDatabaseRepository;
import org.map.socialnetwork.repository.paging.PagingRepository;
import org.map.socialnetwork.service.FriendRequestService;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.MessageService;
import org.map.socialnetwork.service.UserService;
import org.map.socialnetwork.validator.FriendRequestValidator;
import org.map.socialnetwork.validator.FriendshipValidator;
import org.map.socialnetwork.validator.MessageValidator;
import org.map.socialnetwork.validator.UserValidator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Start extends Application {

    Repository<Long, User> userRepository = new UserDatabaseRepository(new UserValidator());
    PagingRepository<Long, User> userPagingRepository = new PagingRepository<>(userRepository, "users");

    Repository<Pair<Long, Long>, Friendship> friendshipRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userRepository);
    PagingRepository<Pair<Long, Long>, Friendship> friendshipPagingRepository = new PagingRepository<>(friendshipRepository, "friendships");


    Repository<Long, FriendRequest> friendRequestRepository = new FriendRequestDatabaseRepository(userRepository,new FriendRequestValidator());
    PagingRepository<Long, FriendRequest> friendRequestPagingRepository = new PagingRepository<>(friendRequestRepository, "friendrequests");



    Repository<Long, Message> messageRepository = new MessageDatabaseRepository(new MessageValidator(), userRepository);
    UserService userService = new UserService(userPagingRepository);
    FriendshipService friendshipService = new FriendshipService(friendshipPagingRepository, userPagingRepository);
    FriendRequestService friendRequestService = new FriendRequestService(friendRequestPagingRepository, friendshipPagingRepository, userPagingRepository);
    MessageService messageService = new MessageService(messageRepository);
    @Override
    public void start(Stage stage) throws IOException{

//        FXMLLoader loaderTest = new FXMLLoader();
//        loaderTest.setLocation(getClass().getResource("views/user/main.fxml"));
//        AnchorPane layoutTest = loaderTest.load();
//        stage.setScene( new Scene(layoutTest));
//
//
//
//        stage.setTitle("Dashboard");
//        stage.show();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/start-view.fxml"));
        AnchorPane layout = loader.load();

        StartController startController = loader.getController();
        startController.setUp(userService, friendshipService, friendRequestService, messageService);

        stage.setScene(new Scene(layout));
        stage.setTitle("Social Network");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}