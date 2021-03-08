package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.gui.ControllerLogin;
import socialnetwork.repository.database.FriendRequestDatabase;
import socialnetwork.repository.database.FriendshipDatabase;
import socialnetwork.repository.database.MessageDatabase;
import socialnetwork.repository.database.UserDatabase;
import socialnetwork.service.UserService;

public class Main extends Application {
    UserDatabase userDatabase;
    FriendshipDatabase friendshipDatabase;
    MessageDatabase messageDatabase;
    FriendRequestDatabase friendRequestDatabase;
    UserService userService;

    ControllerLogin loginWindow;
    @Override
    public void start(Stage primaryStage) throws Exception{
        userDatabase = new UserDatabase(new UserValidator(),"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
        friendshipDatabase = new FriendshipDatabase(new FriendshipValidator(),"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
        friendRequestDatabase = new FriendRequestDatabase(new FriendRequestValidator(),"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
        messageDatabase = new MessageDatabase(new MessageValidator(),userDatabase,"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
        userService = new UserService(userDatabase,friendshipDatabase,friendRequestDatabase,messageDatabase);


        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        AnchorPane root=loader.load();

        ControllerLogin ctrl=loader.getController();
        ctrl.setService(userService);

        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.setTitle("Login");
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}
