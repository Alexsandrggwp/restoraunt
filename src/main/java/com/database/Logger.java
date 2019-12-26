package com.database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Logger extends BaseRepo{

    public Logger() {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void LOGG(String msg){
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC")));

        try (Connection connection = DriverManager.getConnection(URL, PASSWORD, LOGIN);
             CallableStatement statement = connection.prepareCall("{CALL logg(?,?)}")) {

            statement.setString(1, msg);
            statement.setTimestamp(2, ts);

            statement.execute();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
