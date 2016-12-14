package ubay.route;

import spark.Request;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class CreateListingRoute extends TemplateRenderer {

    public CreateListingRoute() {
        get("/listing/create-template", (req, res) -> renderCreateListingTemplate(req));
        post("/listing/data", (req, res) -> createListing(req));
    }

    private String renderCreateListingTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/createListing.vm", model);
    }

    private String createListing(Request req)  {
        Map<String, Object> model = new HashMap<>();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String item_id = req.queryParams("item_id");
        String starting_price = req.queryParams("starting_price");
        String end_datetime = req.queryParams("end_datetime");
        String bid_id = req.queryParams("bid_id");

        try {
            if (currentDate.after(dateFormat.parse(end_datetime))) {
                System.out.println("THIS IS A REAL DATE BOOYAKASHA");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("This is in the past and therefor impossible please try again ¯\\_(ツ)_/¯");
        }

        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT  INTO auction VALUES (item_id, starting_price, bid_id, end_datetime");
            preparedStatement.setString(1, item_id);
            preparedStatement.setString(2, starting_price);
            preparedStatement.setInt(3, 1);
            preparedStatement.setString(4, end_datetime);


            int result = preparedStatement.executeUpdate();
            if (result != 0) {
                System.out.println("Success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Listing cannot be created at this time.  ¯\\_(ツ)_/¯");
        }

        return renderTemplate("velocity/home.vm", new HashMap());
    }
}





