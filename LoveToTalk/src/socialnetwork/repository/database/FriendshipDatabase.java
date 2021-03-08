package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class FriendshipDatabase extends InMemoryRepository<Tuple<Long,Long>, Friendship> {
    private String url;
    private String username;
    private String password;

    public FriendshipDatabase(){}
    public FriendshipDatabase(Validator<Friendship> validator, String url, String username, String password) {
        super(validator);
        this.url=url;
        this.username=username;
        this.password=password;
        loadData();

    }

    protected void loadData() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("friend1");
                Long id2 = resultSet.getLong("friend2");
                Tuple<Long, Long> idFriendship = new Tuple<Long, Long>(id1, id2);

                String date = resultSet.getString("date");
                List<String> attr= Arrays.asList(date.split("-"));
                Friendship friendship = new Friendship(LocalDateTime.of(Integer.parseInt(attr.get(0)),Integer.parseInt(attr.get(1)),Integer.parseInt(attr.get(2)),0,0));
                friendship.setId(idFriendship);
                super.save(friendship);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Friendship save(Friendship entity){

        Tuple<Long,Long> compl=new Tuple<Long,Long>(entity.getId().getRight(),entity.getId().getLeft());
        Friendship p=findOne(compl);
        if(p!=null)
            throw new ValidationException("Id already exists!");
        Friendship friendship=super.save(entity);

        if (friendship==null)
        {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships VALUES(?,?,?)");
            ) {
                statement.setInt(1,Integer.parseInt(entity.getId().getRight().toString()));
                statement.setInt(2,Integer.parseInt(entity.getId().getLeft().toString()));
                statement.setString(3, entity.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                statement.execute();
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return friendship;
    }

    @Override
    public Friendship delete(Tuple<Long,Long> longLongTuple) {
        Friendship p=findOne(longLongTuple);
        if(p==null)
            return null;
        Friendship e = super.delete(p.getId());
        if(e!=null) {
            try(Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE friend1 = ? AND friend2 = ?");
            ){
                statement.setInt(1, Integer.parseInt(e.getId().getLeft().toString()));
                statement.setInt(2, Integer.parseInt(e.getId().getRight().toString()));
                statement.execute();
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return e;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> longLongTuple) {
        Tuple<Long,Long> compl=new Tuple<Long,Long>(longLongTuple.getRight(),longLongTuple.getLeft());
        Friendship friendship=super.findOne(longLongTuple);
        if(friendship==null)
            return super.findOne(compl);
        else
            return friendship;
    }


}
