package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import entities.Dish;
import entities.Order;
import entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepo extends BaseRepo{

    private DishRepo dishRepo = new DishRepo();

    public OrderRepo() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*public List<Order> getAllOrders(){
        String getAllOrders = "select * from orders";
        List<Order> allOrdersList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             Statement statement = connection.createStatement()) {

            ResultSet orderResultSet = statement.executeQuery(getAllOrders);

            while (orderResultSet.next()){
                Order order = new Order();
                order.setId(orderResultSet.getInt("order_id"));
                order.setOrderAmount(orderResultSet.getInt("order_value"));
                order.setTableId(orderResultSet.getInt("table_id"));
                order.setClientId(orderResultSet.getInt("client_id"));
                order.setWaiterId(orderResultSet.getInt("waiter_id"));
                allOrdersList.add(order);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allOrdersList;
    }*/

    public void reserveOrder(int orderId, int tableId, int clientId){
        String addOrder = "insert into orders (order_id, table_id, client_id) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(addOrder)) {

            statement.setInt(1, orderId);
            statement.setInt(2, tableId);
            statement.setInt(3, clientId);

            statement.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getUsersOrders(int userId){
        String getUsersOrders = "select * from orders where client_id = ?";
        List<Order> result = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getUsersOrders)) {

            statement.setInt(1, userId);

            ResultSet orderResultSet = statement.executeQuery();

            while (orderResultSet.next()){
                Order order = new Order();
                User client = new User();
                User waiter = new User();
                waiter.setId(orderResultSet.getInt("waiter_id"));
                client.setId(orderResultSet.getInt("client_id"));
                order.setId(orderResultSet.getInt("order_id"));
                order.setOrderAmount(orderResultSet.getInt("order_value"));
                order.setTableId(orderResultSet.getInt("table_id"));
                order.setClient(client);
                order.setWaiter(waiter);
                result.add(order);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        fillUpOrders(result);
        return result;
    }

    private void fillUpOrders(List<Order> orders) {
        String getOrderDishes = "select dish_id, repeats from dish_order where order_id = ?";
        List<Dish> dishes = dishRepo.getAllDishes();
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN)){
            for (Order order : orders) {
                PreparedStatement statement = connection.prepareStatement(getOrderDishes);
                statement.setInt(1, order.getId());

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    for(Dish dish: dishes){
                        if (resultSet.getInt(1) == dish.getId()){
                            for (int i = 0; i < resultSet.getInt(2); i++){
                                order.addDish(dish);
                            }
                        }
                    }
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isOrderPresent(int orderId){
        String isOrderPresent = "select * from orders where order_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(isOrderPresent)) {

            statement.setInt(1, orderId);

            ResultSet orderResultSet = statement.executeQuery();

            return !orderResultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int getOrderTableId(int orderId){
        String getOrderTableId = "select table_id from orders where order_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getOrderTableId)) {

            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt("table_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteOrder(int orderId){
        String deleteOrder = "delete from orders where order_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(deleteOrder)) {

            statement.setInt(1, orderId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getAllTableOrders(int tableId){
        String getAllTableOrders = "select * from orders where table_id = ?";

        List<Order> result = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getAllTableOrders)) {

            statement.setInt(1, tableId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Order order = new Order();
                User client = new User();
                User waiter = new User();
                waiter.setId(resultSet.getInt("waiter_id"));
                client.setId(resultSet.getInt("client_id"));
                order.setId(resultSet.getInt("order_id"));
                order.setTableId(resultSet.getInt("table_id"));
                order.setClient(client);
                order.setWaiter(waiter);
                result.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
