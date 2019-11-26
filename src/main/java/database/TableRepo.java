package database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import entities.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableRepo extends BaseRepo{

    public TableRepo() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reserveTable(int tableId, int userId){
        String insertTableReservation = "update tables set user_id = ? where table_id = ?";

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(insertTableReservation)) {

            statement.setInt(1, userId);
            statement.setInt(2,  tableId);
            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Table> getAllTables(){
        String allTables = "select * from tables";
        List<Table> allTablesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(allTables);

            while (resultSet.next()){
                Table table = new Table();
                table.setId(resultSet.getInt("table_id"));
                table.setClientId(resultSet.getInt("user_id"));
                allTablesList.add(table);
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allTablesList;
    }

    public List<Table> getUsersTables(int userId){
        String getUsersOrders = "select * from tables where user_id = ?";
        List<Table> result = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getUsersOrders)) {

            statement.setInt(1, userId);

            ResultSet orderResultSet = statement.executeQuery();

            while (orderResultSet.next()){
                Table table = new Table();
                table.setId(orderResultSet.getInt("table_id"));
                table.setClientId(orderResultSet.getInt("user_id"));
                result.add(table);
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getSumOfTableOrders(int tableId){
        String getTableOrders = "select count(*) as sum from orders where table_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(getTableOrders)) {

            statement.setInt(1, tableId);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt("sum");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setTableFree(int tableId){
        String setFree = "update tables set user_id = null where table_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             PreparedStatement statement = connection.prepareStatement(setFree)) {

            statement.setInt(1, tableId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
