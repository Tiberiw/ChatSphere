package org.map.socialnetwork.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.map.socialnetwork.Start;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.service.UserService;
import org.map.socialnetwork.utils.Events.UserEvent;
import org.map.socialnetwork.utils.Events.UserFriendshipEvent;
import org.map.socialnetwork.utils.Observer.Observer;

import java.io.IOException;
import java.util.stream.StreamSupport;

public class AdminUserSceneController implements Observer{

    @FXML
    Button addUserButton;

    @FXML
    Button updateUserButton;

    @FXML
    Button removeUserButton;

    @FXML
    TableView<User> tableViewUsers;

    @FXML
    TableColumn<User, String> firstNameField;

    @FXML
    TableColumn<User, String> lastNameField;

    UserService userService;

    ObservableList<User> model = FXCollections.observableArrayList();

    public void updateModel() {
        model.setAll(StreamSupport.stream(userService.getAll().spliterator(),false).toList());
//        model.forEach(System.out::println);
    }

    public void setService(UserService service) {
        /// TODO observer
        userService = service;
        userService.addObserver(this);
        updateModel();
    }

    @FXML
    public void initialize() {
        firstNameField.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameField.setCellValueFactory(new PropertyValueFactory<>("lastName"));

//        updateModel();
        tableViewUsers.setItems(model);
    }

    @FXML
    public void handleSelection(javafx.scene.input.MouseEvent mouseEvent) {

        User user = tableViewUsers.getSelectionModel().getSelectedItem();
        if(user == null) {
            updateUserButton.setDisable(true);
            removeUserButton.setDisable(true);
        } else {
            updateUserButton.setDisable(false);
            removeUserButton.setDisable(false);
        }


    }

    @FXML
    public void addUser(ActionEvent event) {
        showDialog(null);
    }

    @FXML
    public void updateUser(ActionEvent event) {
        User user = tableViewUsers.getSelectionModel().getSelectedItem();
        showDialog(user);
    }

    @FXML
    public void removeUser(ActionEvent event) {
        User user = tableViewUsers.getSelectionModel().getSelectedItem();
        userService.removeFromRepository(user.getID());
    }

    @FXML
    public void showDialog(User user) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Start.class.getResource("views/admin/user-edit-scene.fxml"));

            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(layout));
            dialogStage.setTitle("Add User");

            AdminUserEditSceneController adminUserEditSceneController = loader.getController();
            adminUserEditSceneController.setService(userService, user);

            dialogStage.show();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update() {
        updateModel();
    }
}
