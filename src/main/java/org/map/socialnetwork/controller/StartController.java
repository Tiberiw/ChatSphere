package org.map.socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.map.socialnetwork.Start;
import org.map.socialnetwork.controller.admin.AdminDashboardController;
import org.map.socialnetwork.controller.admin.AdminFriendshipSceneController;
import org.map.socialnetwork.controller.admin.AdminUserSceneController;
import org.map.socialnetwork.controller.user.UserDashboardController;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.service.FriendRequestService;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.MessageService;
import org.map.socialnetwork.service.UserService;

import java.io.IOException;
import java.util.Optional;

public class StartController {

    UserService userService;
    FriendshipService friendshipService;
    FriendRequestService friendRequestService;
    MessageService messageService;

    @FXML
    TextField usernameField;
    @FXML
    Button loginButton;

    public void setUp(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
    }

    public void login() throws IOException {

        String username = usernameField.getText();
        if(username.equals("admin")) {

            Stage stage = new Stage();

            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(Start.class.getResource("views/admin/user-scene.fxml"));
            BorderPane userSceneBorderPane = loader1.load();
            AdminUserSceneController userSceneController = loader1.getController();
            userSceneController.setService(userService);


            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(Start.class.getResource("views/admin/friendship-scene.fxml"));
            BorderPane friendshipSceneBorderPane = loader2.load();
            AdminFriendshipSceneController friendshipSceneController = loader2.getController();
            friendshipSceneController.setService(friendRequestService, friendshipService ,userService);


            FXMLLoader loader3 = new FXMLLoader();
            loader3.setLocation(Start.class.getResource("views/admin/main-scene.fxml"));
            BorderPane mainLayout = loader3.load();
            AdminDashboardController mainController = loader3.getController();
            mainController.setSubScenes(userSceneBorderPane, friendshipSceneBorderPane);
            stage.setTitle("Admin Dashboard");
            stage.setScene(new Scene(mainLayout));

            stage.show();


        }else {
            Optional<User> user = userService.getFromRepository(Long.valueOf(username));
            if(user.isPresent()) {

                Stage stage = new Stage();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Start.class.getResource("views/user/main-scene.fxml"));
                AnchorPane layout = loader.load();
                UserDashboardController userDashboardController = loader.getController();
                userDashboardController.setUp(user.get(), userService, friendshipService, friendRequestService, messageService);
                stage.setScene(new Scene(layout));
                stage.setTitle("User Dashboard");
                stage.show();
            }

        }

    }
}
