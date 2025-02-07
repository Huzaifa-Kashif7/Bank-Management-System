package bank.management.system;

import java.sql.*;

public class _Con {
    Connection connection;
    Statement statement;
    public _Con() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankSystem", "root", "yousif123");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
