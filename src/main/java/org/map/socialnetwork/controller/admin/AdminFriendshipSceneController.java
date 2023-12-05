package org.map.socialnetwork.controller.admin;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.map.socialnetwork.Start;
import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.service.FriendRequestService;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.UserService;
import org.map.socialnetwork.utils.Events.*;
import org.map.socialnetwork.utils.Observer.Observer;

import java.io.IOException;
import java.util.stream.StreamSupport;

public class AdminFriendshipSceneController implements Observer{

///TODO OBSERVER PE USERS
    @FXML
    TableView<Friendship> tableViewFriendships;
    @FXML
    TableColumn<Friendship, User> firstUser;
    @FXML
    TableColumn<Friendship, User> secondUser;
    @FXML
    TableColumn<Friendship, String> friendsFrom;

    @FXML
    Button addFriendshipButton;
    @FXML
    Button updateFriendshipButton;
    @FXML
    Button removeFriendshipButton;

    FriendRequestService friendRequestService;
    FriendshipService friendshipService;
    UserService userService;
    ObservableList<Friendship> model = FXCollections.observableArrayList();

    public void updateModel() {
        model.setAll(StreamSupport.stream(friendshipService.getAll().spliterator(),false).toList());
    }

    @FXML
    public void initialize() {
        tableViewFriendships.setItems(model);

        firstUser.setCellValueFactory(new PropertyValueFactory<>("firstUser"));
        secondUser.setCellValueFactory(new PropertyValueFactory<>("secondUser"));
        friendsFrom.setCellValueFactory(new PropertyValueFactory<>("date"));


    }

    public void setService(FriendRequestService friendRequestService, FriendshipService service, UserService userService) {
        this.friendshipService = service;
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.friendshipService.addObserver(this);
        this.userService.addObserver(this);
        this.friendRequestService.addObserver(this);
        updateModel();
    }

    @FXML
    public void handleSelection(javafx.scene.input.MouseEvent mouseEvent) {

        Friendship friendship = tableViewFriendships.getSelectionModel().getSelectedItem();
        if(friendship == null) {
            updateFriendshipButton.setDisable(true);
            removeFriendshipButton.setDisable(true);
        } else {
            updateFriendshipButton.setDisable(false);
            removeFriendshipButton.setDisable(false);
        }


    }

    @FXML
    public void addFriendship(ActionEvent event) {
        showDialog(null);
        tableViewFriendships.getSelectionModel().clearSelection();

    }

    @FXML
    public void updateFriendship(ActionEvent event) {
        Friendship friendship = tableViewFriendships.getSelectionModel().getSelectedItem();
        showDialog(friendship);
        tableViewFriendships.getSelectionModel().clearSelection();
        updateFriendshipButton.setDisable(true);
        removeFriendshipButton.setDisable(true);
    }

    @FXML
    public void removeFriendship(ActionEvent event) {
        Friendship friendship = tableViewFriendships.getSelectionModel().getSelectedItem();
        friendshipService.removeFromRepository(friendship.getFirstUser().getID(), friendship.getSecondUser().getID());
        tableViewFriendships.getSelectionModel().clearSelection();
        updateFriendshipButton.setDisable(true);
        removeFriendshipButton.setDisable(true);
    }

    @FXML
    public void showDialog(Friendship friendship) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Start.class.getResource("views/admin/friendship-edit-scene.fxml"));

            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(layout));
            dialogStage.setTitle("Add User");

            AdminFriendshipEditSceneController adminFriendshipEditSceneController = loader.getController();
            adminFriendshipEditSceneController.setService(friendshipService, friendship);

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
