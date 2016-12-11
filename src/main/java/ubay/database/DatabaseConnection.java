package ubay.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection con;

    public DatabaseConnection() {
        connect();
    }

    protected void connect() {
        Properties config = new Properties();
        InputStream file;

        try {
            file = this.getClass().getClassLoader().getResourceAsStream("config/config.properties");
            config.load(file);

            String url = "jdbc:mysql://" + config.getProperty("databaseURL") + ":" + config.getProperty("databasePort") + "/" + config.getProperty("databaseName");
            String user = config.getProperty("databaseUser");
            String password = config.getProperty("databasePassword");

            try {
                con = DriverManager.getConnection(url, user, password);
                System.out.println("Database Connected");
            } catch(SQLException e) {
                e.printStackTrace();
            }

            file.close();
        } catch(IOException e) {
            e.printStackTrace();
        }


    }
}
