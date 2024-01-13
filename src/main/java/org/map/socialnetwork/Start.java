package org.map.socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.map.socialnetwork.controller.AuthenticationController;
import org.map.socialnetwork.controller.StartController;
import org.map.socialnetwork.domain.*;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.repository.database.*;
import org.map.socialnetwork.repository.paging.PagingRepository;
import org.map.socialnetwork.service.*;
import org.map.socialnetwork.validator.*;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Start extends Application {

    Repository<Long, User> userRepository = new UserDatabaseRepository(new UserValidator());
    PagingRepository<Long, User> userPagingRepository = new PagingRepository<>(userRepository, "users");

    Repository<Pair<Long, Long>, Friendship> friendshipRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userRepository);
    PagingRepository<Pair<Long, Long>, Friendship> friendshipPagingRepository = new PagingRepository<>(friendshipRepository, "friendships");


    Repository<Long, FriendRequest> friendRequestRepository = new FriendRequestDatabaseRepository(new FriendRequestValidator(), userRepository);
    PagingRepository<Long, FriendRequest> friendRequestPagingRepository = new PagingRepository<>(friendRequestRepository, "friendrequests");



    Repository<Long, Message> messageRepository = new MessageDatabaseRepository(new MessageValidator(), userRepository);

    Repository<User, UserCredentials> userCredentialsRepository = new UserCredentialsDatabaseRepository(new UserCredentialsValidator(), userRepository);

    UserService userService = new UserService(userPagingRepository);
    FriendshipService friendshipService = new FriendshipService(friendshipPagingRepository, userPagingRepository);
    FriendRequestService friendRequestService = new FriendRequestService(friendRequestPagingRepository, friendshipPagingRepository, userPagingRepository);
    MessageService messageService = new MessageService(messageRepository);
    UserCredentialsService userCredentialsService = new UserCredentialsService(userCredentialsRepository);

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
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String password1 = "asdaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        md.update(password1.getBytes());

        byte[] digest = md.digest();

        StringBuffer hexString = new StringBuffer();

        for (byte b : digest) {
            hexString.append(Integer.toHexString(0xFF & b));
        }



        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("views/start-view.fxml"));

        loader.setLocation(getClass().getResource("views/authentication.fxml"));
        AnchorPane layout = loader.load();




        AuthenticationController authenticationController = loader.getController();
        authenticationController.setUp(userService, friendshipService, friendRequestService, messageService, userCredentialsService);

        stage.setScene(new Scene(layout));

        Image windowIcon = new Image("./images/glob.png");
        stage.getIcons().add(windowIcon);

        stage.setTitle("Social Network");
        stage.setHeight(800);
//        stage.setMaximized(true);
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}