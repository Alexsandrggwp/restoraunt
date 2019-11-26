package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import entities.Dish;

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
        String addDishesToOrder = "insert into dish_order (order_id, dish_id, repeats) value (?,?,1) ";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(addDishesToOrder)) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void increaseRepeats(int orderId, int dishId){
        String increase = "update dish_order set repeats = repeats + 1 where order_id = ? and dish_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(increase)) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decreaseRepeats(int orderId, int dishId){
        String increase = "update dish_order set repeats = repeats - 1 where order_id = ? and dish_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(increase)) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isDishPresent(int orderId, int dishId){
        String increase = "select * from dish_order where order_id = ? and dish_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(increase)) {

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
        String getRepeats = "select repeats from dish_order where order_id = ? and dish_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getRepeats)) {

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
        String getSum = "select sum(repeats) as sum from dish_order where order_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getSum)) {

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
        String delete = "delete from dish_order where order_id = ? and dish_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(delete)) {

            statement.setInt(1, orderId);
            statement.setInt(2, dishId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllDishesFromOrder(int orderId){
        String delete = "delete from dish_order where order_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(delete)) {

            statement.setInt(1, orderId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Dish> getAllOrderedDishes(){
        String getAllOrderedOrders = "select dish_id, repeats from dish_order";
        List<Dish> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getAllOrderedOrders)) {

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
        String getDishesOfOrder = "select d.dish_id, dish_name, dish_cost from dishes d" +
                " inner join dish_order o on d.dish_id = o.dish_id where order_id = ?";
        List<Dish> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getDishesOfOrder)) {

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
