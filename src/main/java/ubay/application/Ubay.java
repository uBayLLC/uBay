package ubay.application;

import ubay.route.*;
import java.sql.*;
import static spark.Spark.*;

public class Ubay {

    public static void main(String [] args) {

        connectDatabase();

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        initializeRoutes();

    }

    public static Connection con;

    private static void initializeRoutes() {
        new IndexRoute();
        new LoginRoute();
    }

    private static void connectDatabase() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/uBay", "root", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}