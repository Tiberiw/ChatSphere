package org.map.socialnetwork.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.domain.UserComponent_DTO;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.utils.DateTimePicker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AdminFriendshipEditSceneController {

    @FXML
    TableView<UserComponent_DTO> tableView;

    @FXML
    TableColumn<UserComponent_DTO, String> firstNameColumn;

    @FXML
    TableColumn<UserComponent_DTO, String> lastNameColumn;

    @FXML
    TableColumn<UserComponent_DTO, Long> componentNumberColumn;

    @FXML
    DateTimePicker dateTimePicker;

    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;
    FriendshipService friendshipService;
    ObservableList<UserComponent_DTO> model = FXCollections.observableArrayList();
    Friendship friendship;

    @FXML
    public void handleSelection(javafx.scene.input.MouseEvent mouseEvent) {

        ObservableList<UserComponent_DTO> selectedUsers = tableView.getSelectionModel().getSelectedItems();
        Long selectedNumber = (long) selectedUsers.size();

        //TODO olny 2 items can be selected
//        for(int i = 0; i < tableView.getSelectionModel().getSelectedItems().size() - 2; i++) {
//            tableView.getSelectionModel().de
//        }
        saveButton.setDisable(selectedNumber != 2);


    }

    public void setService(FriendshipService service, Friendship friendship) {
        this.friendshipService = service;
        this.friendship = friendship;


        //Set the table view;
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        componentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("componentNumber"));
        updateModel(service.getCommunitiesMap());
        tableView.setItems(model);

        TableView.TableViewSelectionModel<UserComponent_DTO> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        dateTimePicker.setValue(LocalDateTime.now().toLocalDate());

        if(friendship != null) {
            //We have to select the users and block the selection

            Optional<UserComponent_DTO> firstUser = model.stream().filter(u -> u.getID().equals(friendship.getFirstUser().getID())).findFirst();
            Optional<UserComponent_DTO> secondUser = model.stream().filter(u -> u.getID().equals(friendship.getSecondUser().getID())).findFirst();

            firstUser.ifPresent( user -> tableView.getSelectionModel().select(user));
            secondUser.ifPresent(user -> tableView.getSelectionModel().select(user));

            //Unblock save button
            saveButton.setDisable(false);

            tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        }



    }

    public void updateModel(Map<Long, List<User>> allComponents) {
        allComponents.forEach((k,v) -> v.forEach(user -> {
            UserComponent_DTO userDTO = new UserComponent_DTO(user.getID(), user.getFirstName(), user.getLastName(), k);
            model.add(userDTO);
        }));
    }

    @FXML
    public void save(ActionEvent event) {

        ObservableList<UserComponent_DTO> userPair = tableView.getSelectionModel().getSelectedItems();
        Optional<User> firstUser = friendshipService.getFromRepositoryUser(userPair.get(0).getInstance().getID());
        Optional<User> secondUser = friendshipService.getFromRepositoryUser(userPair.get(1).getInstance().getID());
        LocalDateTime date = dateTimePicker.getDateTimeValue();

        if(firstUser.isPresent() && secondUser.isPresent()) {

            if(this.friendship == null) {
                this.friendshipService.saveToRepository(firstUser.get().getID(), secondUser.get().getID(), date.toString());
            }else {
                this.friendshipService.updateToRepository(this.friendship.getID().getFirst(), this.friendship.getID().getSecond(), date.toString());
            }
        }

        cancel(event);
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
