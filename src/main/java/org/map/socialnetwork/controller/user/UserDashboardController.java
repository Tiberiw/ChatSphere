package org.map.socialnetwork.controller.user;

import javafx.beans.binding.Bindings;
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
import org.map.socialnetwork.domain.*;
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

    UserSettings userSettings;
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

    @FXML
    HBox pageButtonContainer;

    @FXML
    Spinner<Integer> pageNumber;

    int selectedPage = 1;

    Message reply = null;
    String mode;

    @FXML
    public void changePageNumber() {
        userSettings.setNrElementsPage(pageNumber.getValue());
        loadPageButtons();
        loadModel();
    }

    public void loadPageButtons() {

        int nrOfElements = friendshipService.getAllFriendsUser(currentUser.getID()).stream().map(Friend_DTO::getUser).toList().size();
        int numberOfPages = nrOfElements / userSettings.getNrElementsPage();

        if(nrOfElements % userSettings.getNrElementsPage() != 0)
            numberOfPages++;

        pageButtonContainer.getChildren().clear();



        if(numberOfPages > 3) {
            int startIndex = selectedPage - 1;
            if(startIndex < 1)
                startIndex = 1;
            int endIndex = selectedPage + 1;
            if(endIndex > numberOfPages)
                endIndex = numberOfPages;
            for(int i = startIndex; i <= endIndex; i++) {
                Button pageNumberButton = new Button(String.valueOf(i));
                pageNumberButton.getStyleClass().add("pageButton");
                if(i == selectedPage)
                    pageNumberButton.getStyleClass().add("selectedPageButton");

                pageNumberButton.setOnAction(e -> {
                    selectedPage = Integer.parseInt(((Button)e.getSource()).getText());
                    loadPageButtons();
                    loadModel();
                });

                pageButtonContainer.getChildren().add(pageNumberButton);
            }

        } else {

            for(int i = 1; i <= numberOfPages; i++) {
                Button pageNumberButton = new Button(String.valueOf(i));
                pageNumberButton.getStyleClass().add("pageButton");
                pageNumberButton.setOnAction(e -> {
                    selectedPage = Integer.parseInt(((Button)e.getSource()).getText());
                    loadPageButtons();
                    loadModel();
                });

                pageButtonContainer.getChildren().add(pageNumberButton);

                if(i == selectedPage)
                    pageNumberButton.getStyleClass().add("selectedPageButton");
            }


        }


    }


    public void loadModel() {



        model.setAll(friendshipService.getAllFriendsUser(currentUser.getID()).stream()
                .map(Friend_DTO::getUser)
                .skip((long) (selectedPage - 1) * userSettings.getNrElementsPage())
                .limit(userSettings.getNrElementsPage())
                .toList());

    }

    public void loadConversation() {

        conversation.setAll(messageService.getChronologicalConv(currentUser,conversationUser));

        ToggleGroup toggleGroup = new ToggleGroup();
        allMessages.setSpacing(10);

        reply = null;
        allMessages.getChildren().clear();
        for(Message message : conversation) {

            if(message.getSender().getID().equals(conversationUser.getID())) {
                RadioButton radioButton = new RadioButton(message.getMessage());
                radioButton.setWrapText(true);
                radioButton.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));
                HBox newItem = new HBox();
                if(message.getReplyTo() != null) {
                    VBox replyItem = new VBox();
                    String allText = message.getReplyTo().getMessage();
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
                if(message.getReplyTo() != null) {
                    VBox replyItem = new VBox();

                    String allText = message.getReplyTo().getMessage();
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
        userSettings = new UserSettings(user, 15);

        userNameLabel.setText("@" + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");

        loadPageButtons();
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
            friendRequestController.setUp(currentUser,userSettings,userService,friendshipService,friendRequestService);


            dialogStage.show();

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @FXML
    public void showSettingsPage() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Start.class.getResource("views/user/settings.fxml"));

            AnchorPane layout = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(layout));
            dialogStage.setTitle("Settings");

            UserSettingsController userSettingsController = loader.getController();
            userSettingsController.setUp(userService, friendshipService, friendRequestService);

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
