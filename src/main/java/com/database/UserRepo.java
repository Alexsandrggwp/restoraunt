package com.database;

import com.entities.Role;
import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.entities.User;
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
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL addUser(?,?,?,?,?)}")) {

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

    public void addWaiterToOrder(int waiterId, int orderId){

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL addWaiterToOrder(?,?)}")) {

            statement.setInt(1, waiterId);
            statement.setInt(2, orderId);

            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getWaiterOfOrder(int orderId){
        User user = new User();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getWaiterOfOrder(?)}")) {

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
        User user = new User();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getClientOfOrder(?)}")) {

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
        List<User> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getAllWaiters()}")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                User waiter = new User();
                waiter.setId(resultSet.getInt("user_id"));
                waiter.setName(resultSet.getString("user_name"));
                waiter.setSurname(resultSet.getString("user_surname"));
                waiter.setRole(Role.valueOf("waiter"));
                result.add(waiter);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Role> getAllRoles(){
        List<Role> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getAllRoles()}")) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                result.add(Role.valueOf(resultSet.getString("role_name")));
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int convertRoleNameToId(String name){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL convertRoleNameToId(?)}")) {

            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                return resultSet.getInt("role_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = new User();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL loadUserByUsername(?)}")) {

            statement.setString(1, s);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                user.setId(resultSet.getInt("user_id"));
                user.setName(resultSet.getString("user_name"));
                user.setSurname(resultSet.getString("user_surname"));
                user.setLogin(resultSet.getString("user_login"));
                user.setPassword(resultSet.getString("user_password"));
                user.setRole(Role.valueOf(resultSet.getString("role_name")));
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
