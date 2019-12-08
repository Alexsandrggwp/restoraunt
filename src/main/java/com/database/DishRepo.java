package com.database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.entities.Dish;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishRepo extends BaseRepo{

    public DishRepo() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Dish> getAllDishes(){
        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getAllDishes()}")) {

            ResultSet orderResultSet = statement.executeQuery();
            while (orderResultSet.next()){
                Dish dish = new Dish();
                dish.setId(orderResultSet.getInt("dish_id"));
                dish.setCost(orderResultSet.getInt("dish_cost"));
                dish.setName(orderResultSet.getString("dish_name"));
                dishes.add(dish);
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    public void addDish(String dishName, int dishCost){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL addDish(?,?)}")) {

            statement.setString(1, dishName);
            statement.setInt(2, dishCost);

            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
