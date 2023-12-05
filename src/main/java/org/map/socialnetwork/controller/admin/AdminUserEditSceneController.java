package org.map.socialnetwork.controller.admin;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.service.UserService;

public class AdminUserEditSceneController {

    UserService userService;

    User user;

    @FXML
    Label firstNameLabel;

    @FXML
    Label lastNameLabel;

    @FXML
    TextField firstNameTextField;

    @FXML
    TextField lastNameTextField;

    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;

    int ok = 0;
    boolean isIncremented1 = false;
    boolean isIncremented2 = false;
    @FXML
    public void cancel(ActionEvent event) {
        clearFields();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void clearFields() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
    }

    public void setFields(User selectedUser) {
        firstNameTextField.setText(selectedUser.getFirstName());
        lastNameTextField.setText(selectedUser.getLastName());
    }

    @FXML
    public void handleFirstNameTextField(KeyEvent event) {
        String text = firstNameTextField.getText();
        if(text.isEmpty() && isIncremented1) {
            ok--;
            isIncremented1 = false;
            saveButton.setDisable(true);
        } else if(!isIncremented1 && !text.isEmpty()) {
            ok++;
            isIncremented1 = true;
        }

        if(ok == 2)
            saveButton.setDisable(false);

    }

    @FXML
    public void handleLastNameTextField(KeyEvent event) {
        String text = lastNameTextField.getText();
        if(text.isEmpty() && isIncremented2) {
            ok--;
            isIncremented2 = false;
            saveButton.setDisable(true);
        } else if(!isIncremented2 && !text.isEmpty()) {
            ok++;
            isIncremented2 = true;
        }

        if(ok == 2)
            saveButton.setDisable(false);
    }

    public void setService(UserService service, User user) {
        this.userService = service;
        this.user = user;
        if(user == null) {
            clearFields();
            saveButton.setDisable(true);
        }
        else {
            setFields(user);

        }

    }

    public void saveUser(ActionEvent event) {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();

        if(this.user == null) {
            this.userService.saveToRepository(firstName, lastName);
        }else {
            System.out.println(this.user.toString());

            this.userService.updateToRepository(user.getID(), firstName, lastName);
        }
        cancel(event);
    }
}
