package com.database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.entities.Dish;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishOrderRepo extends BaseRepo{

    private DishRepo dishRepo = new DishRepo();

    public DishOrderRepo() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDishToOrder(int orderId, int dishId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL addDishToOrder(?, ?)}")) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void increaseRepeats(int orderId, int dishId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL increaseRepeats(?, ?)}")) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decreaseRepeats(int orderId, int dishId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL decreaseRepeats(?, ?)}")) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isDishPresent(int orderId, int dishId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL isDishPresent(?, ?)}")) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getRepeats(int orderId, int dishId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getRepeats(?, ?)}")) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            return resultSet.getInt("repeats");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSumOfOrderDishes(int orderId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getSum(?)}")) {

            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("sum");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteDishFromOrder(int orderId, int dishId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL deleteDishFromOrder(?, ?)}")) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllDishesFromOrder(int orderId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL deleteAllDishesFromOrder(?)}")) {

            statement.setInt(1, orderId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Dish> getAllOrderedDishes(){
        List<Dish> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getAllOrderedDishes()}")) {

            List<Dish> dishes = dishRepo.getAllDishes();

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                for (Dish dish : dishes) {
                    if(resultSet.getInt("dish_id") == dish.getId()){
                        for (int i = 0; i < resultSet.getInt("repeats"); i++){
                            result.add(dish);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Dish> getDishesOfOrder(int orderId){
        List<Dish> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getDishesOfOrder(?)}")) {

            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setCost(resultSet.getInt("dish_cost"));
                result.add(dish);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
