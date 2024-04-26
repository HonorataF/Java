package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    public static Connection connect() {
        Connection con = null;
        try {
            // Loading the class will automatically register it
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:C:\\Honorata\\Apps\\AppService\\SMS.db");
            System.out.println("Connection to SQLite has been established.");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load JDBC driver: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }
}
