package socialnetwork.repository.database;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class FriendRequestDatabase extends InMemoryRepository<Tuple<Long,Long>, FriendRequest> {
    private String url;
    private String username;
    private String password;

    public FriendRequestDatabase(){}
    public FriendRequestDatabase(Validator<FriendRequest> validator, String url, String username, String password) {
        super(validator);
        this.url=url;
        this.username=username;
        this.password=password;
        loadData();

    }

    protected void loadData() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from \"friendRequests\"\n");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("user1");
                Long id2 = resultSet.getLong("user2");
                Tuple<Long, Long> idFriendship = new Tuple<Long, Long>(id1, id2);

                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(status);
                friendRequest.setId(idFriendship);
                super.save(friendRequest);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    } 

    @Override
    public FriendRequest save(FriendRequest entity){
        FriendRequest friendRequest=super.save(entity);
        if (friendRequest==null)
        {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO \"friendRequests\"\n VALUES(?,?,?)");
            ) {
                statement.setInt(1,Integer.parseInt(entity.getId().getLeft().toString()));
                statement.setInt(2,Integer.parseInt(entity.getId().getRight().toString()));
                statement.setString(3, entity.getStatus());

                statement.execute();
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return friendRequest;
    }

    @Override
    public FriendRequest delete(Tuple<Long,Long> longLongTuple) {
        FriendRequest p=this.findOne(longLongTuple);
        if(p.getId().getLeft()!=longLongTuple.getLeft())
            return null;
        FriendRequest e = super.delete(longLongTuple);
        if(e!=null) {
            try(Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM \"friendRequests\"\n WHERE user1 = ? AND user2 = ?");
            ){
                statement.setInt(1, Integer.parseInt(longLongTuple.getLeft().toString()));
                statement.setInt(2, Integer.parseInt(longLongTuple.getRight().toString()));
                statement.execute();
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return e;
    }

    @Override
    public FriendRequest findOne(Tuple<Long, Long> longLongTuple) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(longLongTuple.getRight(),longLongTuple.getLeft());
        FriendRequest friendRequest=super.findOne(longLongTuple);
        if(friendRequest==null)
            return super.findOne(compl);
        else
            return friendRequest;
    }

    public void update(Tuple<Long,Long> id, String newStatus) {
        FriendRequest friendRequest=this.findOne(id);
        if(friendRequest.getId().getRight()!=id.getRight())
            throw new ValidationException("This friend request does not exist!");
        if(!friendRequest.getStatus().equals("pending"))
            throw new ValidationException("This friend request is not a pending request!");
        if(!newStatus.equals("approved") && !newStatus.equals("rejected"))
            throw new ValidationException("Invalid new status!");
        friendRequest.setStatus(newStatus);
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("UPDATE \"friendRequests\"\n SET status = ? WHERE user1 = ? AND user2 = ?");
        ){
            statement.setString(1,newStatus);
            statement.setInt(2, Integer.parseInt(friendRequest.getId().getLeft().toString()));
            statement.setInt(3, Integer.parseInt(friendRequest.getId().getRight().toString()));
            statement.execute();
        }catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

}
