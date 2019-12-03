package com.database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import com.entities.Table;

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
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL reserveTable(?,?)}")) {

            statement.setInt(1, userId);
            statement.setInt(2,  tableId);
            statement.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Table> getAllTables(){
        List<Table> allTablesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getAllTables()}")) {

            ResultSet resultSet = statement.executeQuery();

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

    public int getSumOfTableOrders(int tableId){
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL getSumOfTableOrders(?)}")) {

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
        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL setTableFree(?)}")) {

            statement.setInt(1, tableId);

            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
