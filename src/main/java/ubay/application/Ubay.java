package ubay.application;

import ubay.database.DatabaseConnection;
import ubay.route.*;

import static spark.Spark.*;

public class Ubay {

    public static void main(String [] args) {

        new DatabaseConnection();

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        initializeRoutes();

    }

    private static void initializeRoutes() {
        new IndexRoute();
        new ItemRoute();
        new CreateAccountRoute();
        new LoginRoute();
        new MyAccountRoute();
        new EditAccountRoute();
        new AuctionRoute();
        new SearchRoute();
    }

}
