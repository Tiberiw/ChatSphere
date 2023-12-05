package org.map.socialnetwork.controller.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.map.socialnetwork.domain.FriendRequest;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.service.FriendRequestService;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.UserService;
import org.map.socialnetwork.utils.Observer.Observer;

public class FriendRequestController implements Observer {

    UserService userService;
    FriendshipService friendshipService;
    FriendRequestService friendRequestService;

    ObservableList<FriendRequest> modelInbox = FXCollections.observableArrayList();
    ObservableList<User> modelSendRequest = FXCollections.observableArrayList();

    User user;

    @FXML
    AnchorPane root;

    @FXML
    SplitPane mainPane;

    @FXML
    BorderPane leftSide;

    @FXML
    Label inboxLabel;

    @FXML
    TableView<FriendRequest> inboxTableView;

    @FXML
    TableColumn<FriendRequest, String> inboxFirstNameField;

    @FXML
    TableColumn<FriendRequest, String> inboxLastNameField;

    @FXML
    Button acceptRequestButton;

    @FXML
    Button declineRequestButton;
    @FXML
    BorderPane rightSide;
    @FXML
    Label addFriendsLabel;
    @FXML
    TableView<User> addFriendsTableView;

    @FXML
    TableColumn<User, String> addFriendsFirstNameField;

    @FXML
    TableColumn<User, String> addFriendsLastNameField;

    @FXML
    Button sendRequestButton;

    public void setUp(User user, UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService) {
        this.user = user;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        userService.addObserver(this);
        friendshipService.addObserver(this);
        friendRequestService.addObserver(this);

//        inboxFirstNameField.setCellValueFactory(param -> param.getValue().firstUserFirstNameProperty());
//        inboxLastNameField.setCellValueFactory(param -> param.getValue().firstUserLastNameProperty());

        inboxFirstNameField.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FriendRequest, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FriendRequest, String> param) {
                return new SimpleStringProperty(param.getValue().getFirstUser().getFirstName());
            }
        });

        inboxLastNameField.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FriendRequest, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FriendRequest, String> param) {
                return new SimpleStringProperty(param.getValue().getFirstUser().getLastName());
            }
        });


        addFriendsFirstNameField.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        addFriendsLastNameField.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        loadModels();

        inboxTableView.setItems(modelInbox);
        addFriendsTableView.setItems(modelSendRequest);

    }

    public void loadModels() {
        modelInbox.setAll(friendRequestService.getPendingRequests(user));
        modelSendRequest.setAll(friendRequestService.getPossibleFriends(user));
    }

    @FXML
    public void acceptRequest() {
        FriendRequest friendRequest = inboxTableView.getSelectionModel().getSelectedItem();
        friendRequestService.approveFriendRequest(friendRequest);
    }

    @FXML
    public void rejectRequest() {
        FriendRequest friendRequest = inboxTableView.getSelectionModel().getSelectedItem();
        friendRequestService.rejectFriendRequest(friendRequest);
    }

    @FXML
    public void sendRequest() {
        User selectedUser = addFriendsTableView.getSelectionModel().getSelectedItem();
        friendRequestService.sendFriendRequest(user, selectedUser);
    }


    @Override
    public void update() {
        loadModels();
    }
}
