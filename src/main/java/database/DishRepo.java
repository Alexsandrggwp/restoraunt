package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import entities.Dish;

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
        String getAllDishes = "select * from dishes";
        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             Statement statement = connection.createStatement()) {

            ResultSet orderResultSet = statement.executeQuery(getAllDishes);
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
}
