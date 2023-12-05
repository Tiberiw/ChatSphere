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
    Repository<Pair<Long, Long>, Friendship> friendshipRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userRepository);
    Repository<Long, FriendRequest> friendRequestRepository = new FriendRequestDatabaseRepository(userRepository,new FriendRequestValidator());
    Repository<Long, Message> messageRepository = new MessageDatabaseRepository(new MessageValidator(), userRepository);
    UserService userService = new UserService(userRepository);
    FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository);
    FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepository, friendshipRepository, userRepository);
    MessageService messageService = new MessageService(messageRepository);
    @Override
    public void start(Stage stage) throws IOException{


//        User user1 = userRepository.findOne(48L).get();
//        User user2 = userRepository.findOne(2L).get();
//        User user3 = userRepository.findOne(3L).get();
//        User user4 = userRepository.findOne(4L).get();
//        List<User> toUsers = Arrays.asList(user2, user3, user4);
//
//
//        Message message = new Message(null, user1, toUsers, "Salut All", LocalDateTime.now(), null);
//        messageRepository.save(message);
////
//        Message message2 = messageRepository.findOne(6L).get();
//        Message replyMessage = new Message(null, user2, Arrays.asList(user1,user3), "Cf ba", LocalDateTime.now(), message2);
//        messageRepository.save(replyMessage);
//
//        Message rpl = messageRepository.findOne(7L).get();
//        System.out.println(rpl);
//
//        messageRepository.findAll().forEach(System.out::println);


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