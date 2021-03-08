package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MessageDatabase extends InMemoryRepository<Long, ReplyMessage> {
    private String url;
    private String username;
    private String password;
    private Random random=new Random();
    private UserDatabase userRepo;

    public MessageDatabase(){}
    public MessageDatabase(Validator<ReplyMessage> validator,UserDatabase userRepo, String url, String username, String password) {
        this.userRepo=userRepo;
        super.validator = validator;
        super.entities=new HashMap<Long, ReplyMessage>();
        this.url=url;
        this.username=username;
        this.password=password;
        loadData();

    }

    protected void loadData() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages ");
             PreparedStatement statementSelectTo = connection.prepareStatement("SELECT * FROM conversation WHERE message = ? ");
             ResultSet resultSet = statement.executeQuery();
        ) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String text = resultSet.getString("text");
                String date = resultSet.getString("date");
                Long reply=0l;
                Long idFrom = 0l;

                statementSelectTo.setInt(1,Integer.parseInt(id.toString()));
                ResultSet resultSetTo=statementSelectTo.executeQuery();
                List<User> to=new ArrayList<User>();
                while(resultSetTo.next()){
                    User user=userRepo.findOne(resultSetTo.getLong("toUser"));
                    to.add(user);
                    reply= resultSetTo.getLong("replies");
                    idFrom = resultSetTo.getLong("fromUser");
                }
                resultSetTo.close();
                User fromUser=userRepo.findOne(idFrom);
                ReplyMessage message;
                if(reply==null){
                    message = new ReplyMessage(fromUser, to, text, LocalDateTime.parse(date),null);
                    message.setId(id);
                }
                else{
                    Message toReply = findOne(reply);
                    message = new ReplyMessage(fromUser, to, text, LocalDateTime.parse(date),toReply);
                    message.setId(id);
                }
                super.save(message);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public ReplyMessage save(ReplyMessage entity){
        Long id= random.nextLong()%1000;
        if(id<0);
        id=id*(-1);
        while(findOne(id)!=null){
            id= random.nextLong();
            if(id<0);
            id=id*(-1);
        }
        entity.setId(id);
        ReplyMessage msg=super.save(entity);
        if (msg==null)
        {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statementMessages = connection.prepareStatement("INSERT INTO messages VALUES(?,?,?)");
            ) {
                statementMessages.setInt(1, Integer.parseInt(entity.getId().toString()));
                statementMessages.setString(2, entity.getMessage());
                statementMessages.setString(3, entity.getDate().toString());
                statementMessages.execute();
                for (User to : entity.getTo()) {
                    PreparedStatement statementConversation = connection.prepareStatement("INSERT INTO conversation (fromUser, toUser, message, replies) VALUES(?,?,?,?)");
                    statementConversation.setInt(1, Integer.parseInt(entity.getFrom().getId().toString()));
                    statementConversation.setInt(2, Integer.parseInt(to.getId().toString()));
                    statementConversation.setInt(3, Integer.parseInt(entity.getId().toString()));
                    if (entity.getMessageToReply() != null) {
                        statementConversation.setInt(4, Integer.parseInt(entity.getMessageToReply().getId().toString()));
                    } else{
                        statementConversation.setNull(4, Types.BIGINT);
                    }
                    statementConversation.execute();
                    statementConversation.close();

                }
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return msg;
    }

//    @Override
//    public ReplyMessage delete(Long id) {
//        ReplyMessage e = super.delete(id);
//        if(e!=null) {
//            try(Connection connection = DriverManager.getConnection(url, username, password);
//                PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
//            ){
//                statement.setInt(1, Integer.parseInt(id.toString()));
//                statement.execute();
//            }catch(SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return e;
//    }

}
