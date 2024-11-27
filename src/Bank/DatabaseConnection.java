package Bank;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private Connection databaseLink;
    public Connection getConnection() {
        try {

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // MySQL database URL and credentials
            String url = "jdbc:mysql://127.0.0.3:3306/banksystem"; // Updated IP address
            String user = "root"; // Your username
            String password = "sdaproject123"; // Your password

            // Establish the connection
            databaseLink = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            databaseLink = null; // Ensure databaseLink is null if connection fails
        }

        return databaseLink;
    }
}
