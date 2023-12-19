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
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.map.socialnetwork.domain.FriendRequest;
import org.map.socialnetwork.domain.Friend_DTO;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.domain.UserSettings;
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

    UserSettings userSettings;

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

    @FXML
    HBox pageButtonContainerInbox;

    @FXML
    HBox pageButtonContainerSendRequest;

    private int selectedPageInbox = 1;
    private int selectedPageRequests = 1;



    public void loadPageButtonsInbox() {


        int nrOfElements = friendRequestService.getPendingRequests(user).size();
        int numberOfPages = nrOfElements / userSettings.getNrElementsPage();

        if(nrOfElements % userSettings.getNrElementsPage() != 0)
            numberOfPages++;


        pageButtonContainerInbox.getChildren().clear();

        if(numberOfPages > 3) {
            int startIndex = selectedPageInbox - 1;
            if(startIndex < 1)
                startIndex = 1;
            int endIndex = selectedPageInbox + 1;
            if(endIndex > numberOfPages)
                endIndex = numberOfPages;
            for(int i = startIndex; i <= endIndex; i++) {
                Button pageNumberButton = new Button(String.valueOf(i));
                pageNumberButton.getStyleClass().add("pageButton");
                if(i == selectedPageInbox)
                    pageNumberButton.getStyleClass().add("selectedPageButton");

                pageNumberButton.setOnAction(e -> {
                    selectedPageInbox = Integer.parseInt(((Button)e.getSource()).getText());
                    loadPageButtonsInbox();
                    loadModels();
                });

                pageButtonContainerInbox.getChildren().add(pageNumberButton);
            }

        } else {

            for(int i = 1; i <= numberOfPages; i++) {
                Button pageNumberButton = new Button(String.valueOf(i));
                pageNumberButton.getStyleClass().add("pageButton");
                pageNumberButton.setOnAction(e -> {
                    selectedPageInbox = Integer.parseInt(((Button)e.getSource()).getText());
                    loadPageButtonsInbox();
                    loadModels();
                });

                pageButtonContainerInbox.getChildren().add(pageNumberButton);

                if(i == selectedPageInbox)
                    pageNumberButton.getStyleClass().add("selectedPageButton");
            }


        }


    }



    public void loadPageButtonsSendRequest() {


        int nrOfElements = friendRequestService.getPossibleFriends(user).size();
        int numberOfPages = nrOfElements / userSettings.getNrElementsPage();

        if(nrOfElements % userSettings.getNrElementsPage() != 0)
            numberOfPages++;


        pageButtonContainerSendRequest.getChildren().clear();

        if(numberOfPages > 3) {
            int startIndex = selectedPageRequests - 1;
            if(startIndex < 1)
                startIndex = 1;
            int endIndex = selectedPageRequests + 1;
            if(endIndex > numberOfPages)
                endIndex = numberOfPages;
            for(int i = startIndex; i <= endIndex; i++) {
                Button pageNumberButton = new Button(String.valueOf(i));
                pageNumberButton.getStyleClass().add("pageButton");
                if(i == selectedPageRequests)
                    pageNumberButton.getStyleClass().add("selectedPageButton");

                pageNumberButton.setOnAction(e -> {
                    selectedPageRequests = Integer.parseInt(((Button)e.getSource()).getText());
                    loadPageButtonsSendRequest();
                    loadModels();
                });

                pageButtonContainerSendRequest.getChildren().add(pageNumberButton);
            }

        } else {

            for(int i = 1; i <= numberOfPages; i++) {
                Button pageNumberButton = new Button(String.valueOf(i));
                pageNumberButton.getStyleClass().add("pageButton");
                pageNumberButton.setOnAction(e -> {
                    selectedPageRequests = Integer.parseInt(((Button)e.getSource()).getText());
                    loadPageButtonsSendRequest();
                    loadModels();
                });

                pageButtonContainerSendRequest.getChildren().add(pageNumberButton);

                if(i == selectedPageRequests)
                    pageNumberButton.getStyleClass().add("selectedPageButton");
            }


        }


    }





    public void setUp(User user,UserSettings userSettings ,UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService) {
        this.user = user;
        this.userSettings = userSettings;
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


        loadPageButtonsInbox();
        loadPageButtonsSendRequest();
        loadModels();

        inboxTableView.setItems(modelInbox);
        addFriendsTableView.setItems(modelSendRequest);

    }

    public void loadModels() {

        modelInbox.setAll(friendRequestService.getPendingRequests(user)
                .stream()
                .skip((long) (selectedPageInbox - 1) * userSettings.getNrElementsPage() )
                .limit(userSettings.getNrElementsPage())
                .toList());


        modelSendRequest.setAll(friendRequestService.getPossibleFriends(user).stream()
                                                    .skip((long) (selectedPageRequests - 1) * userSettings.getNrElementsPage())
                                                    .limit(userSettings.getNrElementsPage())
                                                    .toList());
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
