package socialnetwork.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import socialnetwork.config.observer.Observer;
import socialnetwork.domain.FriendRequestDTO;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ControllerChat implements Observer {

    UserService userService;
    Long userId;
    Long toId;
    ObservableList<User> modelFriends = FXCollections.observableArrayList();
    ObservableList<ReplyMessage> modelMessages=FXCollections.observableArrayList();

    @FXML
    ListView<User> listViewFriends;
    @FXML
    ListView<ReplyMessage> listViewMessages;
    @FXML
    TextField fieldMessageText;

    @FXML
    public void initialize(){
        listViewFriends.setItems(modelFriends);
        listViewMessages.setItems(modelMessages);

    }

    public void setService(UserService userService){
        this.userService=userService;
    }

    public void prepareService(){
        userService.addObserver(this);
        modelFriends.setAll(userService.findUser(userId).getFriends());
        listViewFriends.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                User selected=listViewFriends.getSelectionModel().getSelectedItem();
                toId=selected.getId();
                modelMessages.setAll(userService.conversation(userId, toId));
            }
        });

    }

    public void setUserId(Long id){
        this.userId = id;
    }

    @Override
    public void update() {
        modelMessages.setAll(userService.conversation(userId, toId));

    }

    public void handleSendButton(){
        List<Long> to=new ArrayList<Long>();
        to.add(toId);
        String text=fieldMessageText.getText();
        if(!text.equals("")){
            userService.addMessage(userId,to,text, LocalDateTime.now());
           // modelMessages.setAll(userService.conversation(userId, toId));
            fieldMessageText.clear();
        }
    }

}
