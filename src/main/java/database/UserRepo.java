package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import entities.Dish;
import entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo extends BaseRepo implements UserDetailsService{

    public UserRepo() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addUser(String name, String surname, String login, String password, int role){
        String addUser = "insert into users values(?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(addUser)) {

            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setString(3, login);
            statement.setString(4, password);
            statement.setInt(5, role);

            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWaiterToOrder(int orderId, int waiterId){
        String addWaiterToTable = "update orders set waiter_id = ? where order_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(addWaiterToTable)) {

            statement.setInt(1, waiterId);
            statement.setInt(2, orderId);

            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getWaiterOfOrder(int orderId){
        String getWaiterOfOrder = "select user_name, user_surname from users inner join orders" +
                " on user_id = waiter_id where order_id = ?";
        User user = new User();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getWaiterOfOrder)) {

            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                user.setName(resultSet.getString("user_name"));
                user.setSurname(resultSet.getString("user_surname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getClientOfOrder(int orderId){
        String getClientOfOrder = "select user_name, user_surname from users inner join orders" +
                " on user_id = client_id where order_id = ?";
        User user = new User();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getClientOfOrder)) {

            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                user.setName(resultSet.getString("user_name"));
                user.setSurname(resultSet.getString("user_surname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllWaiters(){
        String getAllWaiters = "select * from users where user_role = 2";
        List<User> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getAllWaiters)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                User waiter = new User();
                waiter.setId(resultSet.getInt("user_id"));
                waiter.setName(resultSet.getString("user_name"));
                waiter.setSurname(resultSet.getString("user_surname"));
                waiter.setRoleId(2);
                result.add(waiter);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = new User();
        user.setLogin("u");
        user.setPassword("u");
        return user;
    }
}
