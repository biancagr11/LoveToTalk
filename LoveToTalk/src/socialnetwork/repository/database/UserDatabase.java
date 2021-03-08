package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class UserDatabase extends InMemoryRepository<Long, User> {
    private String url;
    private String username;
    private String password;

    public UserDatabase(){}
    public UserDatabase( Validator<User> validator, String url, String username, String password) {
        super(validator);
        this.url=url;
        this.username=username;
        this.password=password;
        loadData();

    }

    protected void loadData() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery();
        ) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");

                User user = new User(firstName, lastName);
                user.setId(id);
                super.save(user);

            }
        } catch (SQLException ex) {
                ex.printStackTrace();
        }
    }

    @Override
    public User save(User entity){
        User user=super.save(entity);
        if (user==null)
        {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES(?,?,?)");
            ) {
                statement.setInt(1,Integer.parseInt(entity.getId().toString()));
                statement.setString(2, entity.getFirstName());
                statement.setString(3, entity.getLastName());
                statement.execute();
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public User delete(Long id) {
        User e = super.delete(id);
        if(e!=null) {
            try(Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
                ){
                    statement.setInt(1, Integer.parseInt(id.toString()));
                    statement.execute();
            }catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return e;
    }

}

