package socialnetwork.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.service.UserService;


import java.sql.*;

public class ControllerLogin {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    Stage stage;

//    UserDatabase userDatabase;
//    FriendshipDatabase friendshipDatabase;
//    MessageDatabase messageDatabase;
//    FriendRequestDatabase friendRequestDatabase;
    UserService userService;

    public ControllerLogin(){}

    public ControllerLogin(Stage stage) {

        this.stage=stage;
//        userDatabase = new UserDatabase(new UserValidator(),"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
//        friendshipDatabase = new FriendshipDatabase(new FriendshipValidator(),"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
//        friendRequestDatabase = new FriendRequestDatabase(new FriendRequestValidator(),"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
//        messageDatabase = new MessageDatabase(new MessageValidator(),userDatabase,"jdbc:postgresql://localhost:5432/socialnetwork","postgres","klapaucius13");
//        userService = new UserService(userDatabase,friendshipDatabase,friendRequestDatabase,messageDatabase);


    }

    public void handleSigninButton(){
        String username=usernameField.getText();
        String password=passwordField.getText();
        String errors="";
        if(username.equals("")){
            errors+="Username can not be empty! ";
        }
        if(password.equals("")){
            errors+="Password can not be empty! ";
        }
        if(!errors.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText(errors);
            alert.showAndWait();
        }
        else{
            try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "klapaucius13");
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM login WHERE username = ?");
                //ResultSet resultSet = statement.executeQuery();
            ){
                statement.setString(1,username);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()==false){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("This username does not exist!");
                    alert.showAndWait();
                    return;
                }
                else{
                        String tablePassword = resultSet.getString("password");
                        if (!password.equals(tablePassword)) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Warning");
                            alert.setHeaderText(null);
                            alert.setContentText("Invalid password!");
                            alert.showAndWait();
                        } else {
                                Long userid=resultSet.getLong("userid");
                                Stage userStage=new Stage();
                                try{

                                    FXMLLoader loader=new FXMLLoader();
                                    loader.setLocation(getClass().getResource("user.fxml"));
                                    AnchorPane root=loader.load();

                                    ControllerUser ctrl=loader.getController();
                                    ctrl.setUserId(userid);
                                    ctrl.setService(userService);
                                    ctrl.prepareService();
                                    userStage.setTitle("Social Network");
                                    userStage.setScene(new Scene(root, 600, 400));
                                    userStage.show();

                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                        }
                }
                resultSet.close();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
    public void setService(UserService userService){
        this.userService=userService;
    }
}
