package org.map.socialnetwork.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.map.socialnetwork.Start;
import org.map.socialnetwork.controller.admin.AdminFriendshipEditSceneController;
import org.map.socialnetwork.domain.FriendRequest;
import org.map.socialnetwork.domain.Friend_DTO;
import org.map.socialnetwork.domain.Message;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.service.FriendRequestService;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.MessageService;
import org.map.socialnetwork.service.UserService;
import org.map.socialnetwork.utils.Observer.Observer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class UserDashboardController implements Observer {

    UserService userService;
    FriendshipService friendshipService;
    FriendRequestService friendRequestService;
    MessageService messageService;
    User currentUser;
    User conversationUser;
    ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<Message> conversation = FXCollections.observableArrayList();
    @FXML
    ListView<User> friendsListView;

    @FXML
    Button friendRequestsButton;

    @FXML
    Label userNameLabel;

    @FXML
    Label friendNameLabel;

    @FXML
    Button sendMessageButton;

    @FXML
    TextField messageTextField;

    @FXML
    VBox allMessages;

    @FXML
    ScrollPane mainPane;

    Message reply = null;
    String mode;


    public void loadModel() {
        model.setAll(friendshipService.getAllFriendsUser(currentUser.getID()).stream().map(Friend_DTO::getUser).toList());

    }

    public void loadConversation() {

        conversation.setAll(messageService.getChronologicalConv(currentUser,conversationUser));

        ToggleGroup toggleGroup = new ToggleGroup();
        allMessages.setSpacing(10);

        reply = null;
        allMessages.getChildren().clear();
        for(Message message : conversation) {

            if(message.getFrom().getID().equals(conversationUser.getID())) {
                RadioButton radioButton = new RadioButton(message.getMessage());
                radioButton.setWrapText(true);
                radioButton.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));
                HBox newItem = new HBox();
                if(message.getReply() != null) {
                    VBox replyItem = new VBox();
                    String allText = message.getReply().getMessage();
                    if(allText.length() > 20)
                        allText = allText.substring(0, 20) + "...";
                    replyItem.getChildren().add(new Label(allText));
                    replyItem.getChildren().add(radioButton);
                    newItem.getChildren().add(replyItem);
                }else {
                    newItem.getChildren().add(radioButton);
                }

                newItem.setAlignment(Pos.BASELINE_LEFT);
                allMessages.getChildren().add(newItem);
                radioButton.setToggleGroup(toggleGroup);
                radioButton.pressedProperty().addListener((e) -> reply = message);

            } else {
                Label label = new Label(message.getMessage());
                label.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
                label.setWrapText(true);
                HBox newItem = new HBox();
                if(message.getReply() != null) {
                    VBox replyItem = new VBox();

                    String allText = message.getReply().getMessage();
                    if(allText.length() > 20)
                        allText = allText.substring(0, 20) + "...";
                    replyItem.getChildren().add(new Label(allText));
                    replyItem.getChildren().add(label);
                    newItem.getChildren().add(replyItem);
                }else {
                    newItem.getChildren().add(label);

                }

                newItem.setAlignment(Pos.BASELINE_RIGHT);


                allMessages.getChildren().add(newItem);

            }
        }

        mainPane.setVvalue(1);
    }

    public void setUp(User user, UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService) {
        this.currentUser = user;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        userService.addObserver(this);
        friendshipService.addObserver(this);
        friendRequestService.addObserver(this);
        messageService.addObserver(this);

        userNameLabel.setText("@" + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");

        loadModel();
        friendsListView.setItems(model);
        friendsListView.setCellFactory(list -> new ListCell<>(){
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setText(null);
                }else {
                    setText(item.getFirstName() + " " + item.getLastName());
                }
            }
        });
        friendsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void sendMessage() {

        if(!messageTextField.getText().isEmpty()) {
            messageService.sendMessage(currentUser, friendsListView.getSelectionModel().getSelectedItems(), messageTextField.getText(), reply);
            messageTextField.clear();

        }
    }

    @FXML
    public void friendSelection() {
        ObservableList<User> selectedFriends =  friendsListView.getSelectionModel().getSelectedItems();
        if(selectedFriends.size() == 1) {
            conversationUser = selectedFriends.get(0);
            messageTextField.clear();
            loadConversation();
        }

    }

    @FXML
    public void showRequestDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Start.class.getResource("views/user/friendrequest-scene.fxml"));

            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(layout));
            dialogStage.setTitle("Friend Requests");

            FriendRequestController friendRequestController = loader.getController();
            friendRequestController.setUp(currentUser,userService,friendshipService,friendRequestService);


            dialogStage.show();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update() {
        loadModel();
        if(conversationUser != null)
            loadConversation();
    }


}
