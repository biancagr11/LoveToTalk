package socialnetwork.ui;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class Ui {
    private UserService userService;
    public Ui(UserService userService){
        this.userService = userService;

    }
    private void Menu(){

        System.out.println("Menu:\n 1. Add user                    2. Remove user              3. All users\n" +
                                  " 4. Add friendship              5. Remove friendship        6. All friendships\n"+
                                  " 7. Number of comunities        8. The biggest community\n"+
                                  " 9. User's friends             10. User's friends by date\n"+
                                  "11. All messages               12. Add message             13. Reply\n"+
                                  "14. Conversations\n"+
                                  "15. Send friend request        16. Update friend request   17.Show all requests\n "
                +"exit: Exit");
    }

    private void showUsers(){
        this.userService.findAllUsers().forEach(System.out::println);
    }
    private String readString(String message, BufferedReader reader){
        System.out.println(message);
        String string="";
        try {
            string = reader.readLine();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return string;
    }
    private Long readLong(String message, BufferedReader reader){
        System.out.println(message);
        try {
            Long nr = Long.parseLong(reader.readLine());
            return nr;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    private String readDate(String message, BufferedReader reader) {
        while (true) {
            System.out.println(message);
            try {
                String date = reader.readLine();
                if (date.matches("..-..-....")) {
                    return date;
                }
                System.out.println("Invalide date!");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    private List<Long> readList(String message, BufferedReader reader) {
        boolean read=true;
        List<Long> toList = new ArrayList<Long>();
        while(read) {
            String toString[];
            System.out.println(message);
            try {
                String to = reader.readLine();
                toString=to.split(" ");
                for(String s:toString){
                    toList.add(Long.parseLong(s));
                }
                read=false;
            } catch (Exception ex) {
                //read=true;
                System.out.println("Invalide dates!");
                toList.clear();
            }
        }
        return toList;
    }

    private void addUser(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id=readLong("Id: ",reader);
        String firstName=readString("First name: ",reader);
        String lastName=readString("Last name: ",reader);
        User user=new User(firstName,lastName);
        user.setId(id);
        try {
            User response = this.userService.addUser(user);
            if (response != null)
                System.out.println("Id already exists!");
            else {
                System.out.println("User saved!");
            }
        }catch(ValidationException ex) {
            System.out.println("Invalid user: "+ex.getMessage());
        }

    }
    private void removeUser(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id=readLong("User's id: ",reader);
        try{
            User response= userService.deleteUser(id);
            if(response==null)
                System.out.println("Id does not exist!");
            else{
                System.out.println("User removed!");
            }
        }catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }
    }


    //Prietenie


    private void addFriendship(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id1=readLong("First friend: ",reader);
        Long id2=readLong("Second friend: ",reader);
        try{
            Tuple<Long,Long> idFriendship=new Tuple<Long,Long>(id1,id2);
            Friendship friendship=new Friendship(LocalDateTime.now());
            friendship.setId(idFriendship);
            Friendship response = userService.addFriendship(friendship);
            if(response!=null)
                System.out.println("Friendship already exists!");
            else{
                System.out.println("Friendship saved!");
            }
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }

    }

    private void removeFriendship(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id1=readLong("First friend: ",reader);
        Long id2=readLong("Second friend: ",reader);
        try{
            Tuple<Long,Long> idFriendship=new Tuple<Long,Long>(id1,id2);
            Friendship response = userService.removeFriendship(idFriendship);
            if(response==null)
                System.out.println("Friendship does not exist!");
            else{
                System.out.println("Friendship removed!");
            }
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }

    }

    private void showFriendships(){
        this.userService.findAllFriendships().forEach(System.out::println);
    }

    //Rapoarte

    private void numberOfComunities(){
        int nr= userService.nrComunitati();
        System.out.println(nr+" ");
    }

    private void biggestCommunity(){
        Vector<Integer> path= userService.biggestCommunity();
        for(long id:path){
            System.out.println(userService.findUser(id));
        }
    }

    private void usersFriends(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id=readLong("User's id: ",reader);
        try {
            Optional<String> friends = userService.usersFriends(id);
            friends.ifPresent(System.out::println);
        }catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }catch(ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void usersFriendsByDate(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id=readLong("User's id: ",reader);
        Long month=readLong("Month",reader);
        //String date=readDate("Date(dd-MM-yyyy):",reader);
        try{
            Optional<String> friends= userService.usersFriendsByDate(id,month);
            friends.ifPresent(System.out::println);
        }catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
        }catch(ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }

    //Messages
    private void showMessages(){
        this.userService.findAllMessages().forEach(System.out::println);
    }

    private void addMessage(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long from=readLong("From(user's id): ",reader);
        List<Long> to=readList("To(user's ids, separated by spaces: ",reader);
        String text=readString("Message: ", reader);
        try{
            ReplyMessage message=userService.addMessage(from,to,text,LocalDateTime.now());
            System.out.println("Message saved!");
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }
        //String date=readDate("Date(dd-MM-yyyy):",reader);
    }
    private void addReplyMessage(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long from=readLong("Your user ID: ",reader);
        Long reply=readLong("Message ID: ",reader);
        String text=readString("Message: ", reader);
        try{
            ReplyMessage message=userService.replyMessage(from,text,LocalDateTime.now(),reply);
            System.out.println("Message saved!");
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showConversations(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id1=readLong("User 1: ",reader);
        Long id2=readLong("User 2: ",reader);
        try{
            List<ReplyMessage> messages=userService.conversation(id1, id2);
            if(messages.isEmpty())
                System.out.println("There are no messages between these users!\n");
            else
                messages.forEach(System.out::println);
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }

    //FriendRequests
    private void showFriendRequests(){userService.allFriendRequests().forEach(System.out::println);}

    private void addFriendRequest(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id1=readLong("First friend: ",reader);
        Long id2=readLong("Second friend: ",reader);
        try{

            FriendRequest response = userService.addFriendRequest(id1,id2);
            if(response!=null)
                System.out.println("There is already a request sent!");
            else{
                System.out.println("Request sent!");
            }
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void updateFriendRequest(){
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        Long id1=readLong("First friend: ", reader);
        Long id2=readLong("Second friend: ", reader);
        String status=readString("New status: ", reader);
        try{
            userService.updateFriendRequest(id1,id2,status);
            System.out.println("Friend request updated!\n");
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void runUi(){
        Menu();
        String cmd="";
        BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Command: ");
            cmd = reader.readLine();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        while(!cmd.equals("exit")){
            if(cmd.equals("3")){
                showUsers();
            }else if(cmd.equals("1")){
                addUser();
            }else if(cmd.equals("2")){
                removeUser();
            }else if(cmd.equals("4")){
                addFriendship();
            }else if(cmd.equals("5")){
                removeFriendship();
            }else if(cmd.equals("6")){
                showFriendships();
            }else if(cmd.equals("7")) {
                numberOfComunities();
            }else if(cmd.equals("8")) {
                biggestCommunity();
            }else if(cmd.equals("9")) {
                usersFriends();
            }else if(cmd.equals("10")) {
                usersFriendsByDate();
            }else if(cmd.equals("11")) {
                showMessages();
            }else if(cmd.equals("12")) {
                addMessage();
            }else if(cmd.equals("13")) {
                addReplyMessage();
            }else if(cmd.equals("14")) {
                showConversations();
            }else if(cmd.equals("15")) {
                addFriendRequest();
            }else if(cmd.equals("17")) {
                showFriendRequests();
            }else if(cmd.equals("16")) {
                updateFriendRequest();
            }
            try {
                System.out.println("Command: ");
                cmd = reader.readLine();
            }catch (IOException ex){
                System.out.println(ex.getMessage());
            }

        }
        System.out.println("Bye!");
    }
}
