package org.map.socialnetwork.controller.admin;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.tools.Borders;
import org.map.socialnetwork.Start;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminDashboardController {

    @FXML
    BorderPane mainLayout;

    @FXML
    Label greetingLabel;

    @FXML
    ComboBox<String> entityComboBox;

    @FXML
    Button logoutButton;

    BorderPane userScene;
    BorderPane friendshipScene;



    @FXML
    public void initialize() {
        greetingLabel.setText("Hello @Admin");

        entityComboBox.getItems().setAll("User", "Friendship");
        entityComboBox.getSelectionModel().selectFirst();

        entityComboBox.setOnAction( (event) -> {
            String choice = entityComboBox.getValue();
            switch (choice) {
                case "User" : displayScene(userScene);
                break;
                case "Friendship": displayScene(friendshipScene);
                break;
            }
        });


    }

    public void displayScene(BorderPane scene) {
        mainLayout.setCenter(scene);
    }

    public void setSubScenes(BorderPane userScene, BorderPane friendshipScene) {
        this.userScene = userScene;
        this.friendshipScene = friendshipScene;
        displayScene(userScene);
    }

    @FXML
    private void logout(ActionEvent event) {
        Stage stage = (Stage) mainLayout.getScene().getWindow();
        stage.close();
    }

}
