package org.map.socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.map.socialnetwork.Start;
import org.map.socialnetwork.controller.user.UserDashboardController;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.domain.UserCredentials;
import org.map.socialnetwork.service.*;
import org.map.socialnetwork.utils.PasswordManager;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class AuthenticationController {

    UserService userService;
    FriendshipService friendshipService;
    FriendRequestService friendRequestService;
    MessageService messageService;
    UserCredentialsService userCredentialsService;

    @FXML
    TextField firstNameRegisterField;
    @FXML
    TextField lastNameRegisterField;
    @FXML
    TextField emailRegisterField;
    @FXML
    TextField phoneNumberRegisterField;
    @FXML
    PasswordField passwordRegisterField;
    @FXML
    PasswordField confirmPasswordRegisterField;
    @FXML
    TextField emailLoginField;
    @FXML
    PasswordField passwordLoginField;

    @FXML
    AnchorPane loginPane;
    @FXML
    AnchorPane registerPane;
    @FXML
    Button registerButton;
    @FXML
    Button loginButton;

    public void setUp(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, UserCredentialsService userCredentialsService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.userCredentialsService = userCredentialsService;

        switchToRegister();
    }

    @FXML
    public void switchToLogIn() {
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }

    @FXML
    public void switchToRegister() {
        registerPane.setVisible(true);
        loginPane.setVisible(false);
    }

    @FXML
    public void register() {

        String firstName = firstNameRegisterField.getText();

        String lastName = lastNameRegisterField.getText();

        String email = emailRegisterField.getText();

        String phoneNumber = phoneNumberRegisterField.getText();

        String password = passwordRegisterField.getText();

        String confirmPassword = confirmPasswordRegisterField.getText();

        if(!checkFieldsRegister()) {
            return ;
        }

        if(userCredentialsService.checkAccount(email)) {

            showAlert("Register Failed", "Email already in use!");
            return;
        }

        Optional<User> insertedUser = userService.saveToRepository(firstName, lastName);
        insertedUser.ifPresent( user -> {

            String encryptedPassword = PasswordManager.getEncryptedPassword(password);

            userCredentialsService.saveToRepository(user, email, phoneNumber, encryptedPassword);
        });

        authenticate(email,password);


    }

    public void showAlert(String title, String body) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(body);
        alert.show();
    }

    @FXML
    public void login() {

        String email = emailLoginField.getText();

        String password = passwordLoginField.getText();

        if(!checkFieldsLogin()) {
            return ;
        }

        authenticate(email,password);
    }

    private void authenticate(String email, String password) {

        Optional<UserCredentials> userCredentials = userCredentialsService.authenticateUser(email, PasswordManager.getEncryptedPassword(password));
        userCredentials.ifPresent( uc -> {
            try {
                openWindow(uc.getID());
                clearFields();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        if(userCredentials.isEmpty()) {
            if(userCredentialsService.checkAccount(email)) {
                showAlert("Authentication Failed", "Incorrect password");

            } else {
                showAlert("Authentication Failed", "Email does not exist");
            }
        }


    }

    private void openWindow(User user) throws IOException {

        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Start.class.getResource("views/user/main-scene.fxml"));
        AnchorPane layout = loader.load();
        UserDashboardController userDashboardController = loader.getController();
        userDashboardController.setUp(user, userService, friendshipService, friendRequestService, messageService);
        stage.setScene(new Scene(layout));
        stage.setTitle("User Dashboard");
        stage.show();
    }

    private void clearFields() {
        this.emailLoginField.setText("");
        this.passwordLoginField.setText("");
        this.firstNameRegisterField.setText("");
        this.lastNameRegisterField.setText("");
        this.emailRegisterField.setText("");
        this.phoneNumberRegisterField.setText("");
        this.passwordRegisterField.setText("");
        this.confirmPasswordRegisterField.setText("");
    }

    private boolean checkFieldsRegister() {


        if(this.firstNameRegisterField.getText().isEmpty() ||
        this.lastNameRegisterField.getText().isEmpty() ||
        this.emailRegisterField.getText().isEmpty() ||
        this.phoneNumberRegisterField.getText().isEmpty() ||
        this.passwordRegisterField.getText().isEmpty() ||
        this.confirmPasswordRegisterField.getText().isEmpty() ) {
            showAlert("Register Failed", "All fields must be completed");
            return false;
        }

        if(!Objects.equals(this.passwordRegisterField.getText(), this.confirmPasswordRegisterField.getText())) {
            showAlert("Register Failed", "Passwords do not match!");
            return false;
        }

        return true;
    }

    private boolean checkFieldsLogin() {
        if(this.emailLoginField.getText().isEmpty() ||
                this.passwordLoginField.getText().isEmpty()) {
            showAlert("Authentication Failed", "All fields must be completed");
            return false;
        }
        return true;
    }



}
