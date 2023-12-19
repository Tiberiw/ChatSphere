package org.map.socialnetwork.controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import org.map.socialnetwork.service.FriendRequestService;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.UserService;
import org.w3c.dom.events.MouseEvent;

public class UserSettingsController {



    @FXML
    Spinner<Integer> friendRequestNumber;

    @FXML
    Spinner<Integer> friendSuggestionNumber;

    UserService userService;
    FriendshipService friendshipService;
    FriendRequestService friendRequestService;

    public void setUp(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService) {

        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;


    }

    @FXML
    public void changeFriendsPageNumber() {
//        int value = friendsPageNumber.getValue();
//        friendshipService.setPageSizeFriendships(value);
//        userService.setPageSizeUsers(value);
    }

    @FXML
    public void changeFriendRequestNumber() {
        int value = friendRequestNumber.getValue();
        friendRequestService.setPageNumberFriendRequest(value);
        userService.setPageSizeUsers(value);
    }

    @FXML
    public void changeFriendSuggestionNumber() {
        int value = friendSuggestionNumber.getValue();
        userService.setPageSizeUsers(value);
        friendshipService.setPageSizeFriendships(value);
    }


}
