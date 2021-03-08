package socialnetwork.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.observer.Observer;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.FriendRequestDTO;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.*;
import socialnetwork.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControllerUser implements Observer{

    Long userId;
    ObservableList<User> modelFriends = FXCollections.observableArrayList();
    ObservableList<User> modelUsers = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> modelRequests = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> modelSentRequests = FXCollections.observableArrayList();


    UserService userService;

    @FXML
    TableColumn<User, String> tableColumnFriendFirstName;
    @FXML
    TableColumn<User, String> tableColumnFriendLastName;
    @FXML
    TableColumn<User, Long> tableColumnFriendId;
    @FXML
    TableColumn<User, String> tableColumnUserFirstName;
    @FXML
    TableColumn<User, String> tableColumnUserLastName;
    @FXML
    TableColumn<User, Long> tableColumnUserId;
    @FXML
    TableColumn<FriendRequestDTO, Long> tableColumnRequestId;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnRequestStatus;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnRequestUsername;
    @FXML
    TableColumn<FriendRequestDTO, Long> tableColumnSentRequestId;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnSentRequestStatus;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnSentRequestUsername;
    @FXML
    TableView<User> tableViewFriends;
    @FXML
    TableView<User> tableViewAllUsers;
    @FXML
    TableView<FriendRequestDTO> tableViewFriendRequests;
    @FXML
    TableView<FriendRequestDTO> tableViewSentRequests;
    @FXML
    Button deleteButton;
    @FXML
    Label username;
    @FXML
    TextField newFriendField;

    public void prepareService(){

        update();
        User user=userService.findUser(userId);
        username.setText(user.getFirstName()+" "+user.getLastName());
        userService.addObserver(this);

    }

    @FXML
    public void initialize(){

        tableColumnFriendFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnFriendLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnFriendId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableViewFriends.setItems(modelFriends);

        tableColumnUserFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnUserLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnUserId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableViewAllUsers.setItems(modelUsers);

        tableColumnRequestId.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO,Long>("id"));
        tableColumnRequestUsername.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("name"));
        tableColumnRequestStatus.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("status"));
        tableViewFriendRequests.setItems(modelRequests);

        tableColumnSentRequestId.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO,Long>("id"));
        tableColumnSentRequestUsername.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("name"));
        tableColumnSentRequestStatus.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("status"));
        tableViewSentRequests.setItems(modelSentRequests);

        newFriendField.textProperty().addListener(x->handleFilter());

    }

    public void setService(UserService userService){
        this.userService=userService;
    }

    @Override
    public void update(){

        Iterable<User> userIterable=userService.findAllUsers();
        List<User> userList=new ArrayList<User>();
        userIterable.forEach(userList::add);
        modelUsers.setAll(userList);

        modelFriends.setAll(userService.findUser(userId).getFriends());
        modelSentRequests.setAll(userService.sentRequests(userId));
        modelRequests.setAll(userService.userRequests(userId));
    }

    public void setUserId(Long id){
        this.userId = id;
    }

    public void handleDeleteButton(){
        User selected=tableViewFriends.getSelectionModel().getSelectedItem();
        if(selected!=null){
            userService.removeFriendship(new Tuple<Long,Long>(selected.getId(),userId));
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("You have to select a friend!");
            alert.showAndWait();
        }
    }

    public void handleFilter(){
        Predicate<User> namePredicate=x->(x.getFirstName()+" "+x.getLastName()).startsWith(newFriendField.getText());

        Iterable<User> userIterable=userService.findAllUsers();
        List<User> userList=new ArrayList<User>();
        userIterable.forEach(userList::add);

        modelUsers.setAll(
                userList.stream()
                .filter(namePredicate)
                .collect(Collectors.toList())
        );
    }

    public void handleSendFriendRequest(){
        User selected=tableViewAllUsers.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try {
                userService.addFriendRequest(userId, selected.getId());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Request sent!");
                alert.showAndWait();
            }catch (ValidationException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("You have to select a friend!");
            alert.showAndWait();
        }
    }

    public void handleCancelFriendRequest(){
        FriendRequestDTO selected=tableViewSentRequests.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try {
                FriendRequest response=userService.deleteFriendRequest(userId, selected.getId());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                if(response!=null)
                    alert.setContentText("Request canceled!");
                else
                    alert.setContentText("Could not cancel this request");
                alert.showAndWait();
            }catch (ValidationException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("You have to select a friend!");
            alert.showAndWait();
        }
    }

    public void handleChat(){
        Stage userStage=new Stage();
        try{

            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("chat.fxml"));
            AnchorPane root=loader.load();

            ControllerChat ctrl=loader.getController();
            ctrl.setService(userService);
            ctrl.setUserId(userId);
            ctrl.prepareService();
            userStage.setTitle("Chat");
            userStage.setScene(new Scene(root, 545, 430));
            userStage.show();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
