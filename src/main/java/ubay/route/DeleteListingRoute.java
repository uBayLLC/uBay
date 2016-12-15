package ubay.route;

import spark.Request;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class DeleteListingRoute extends TemplateRenderer {

    public DeleteListingRoute() {
        get("/item/delete-template", (req, res) -> renderDeleteListingTemplate(req));
        post("/item/data", (req, res) -> parseDeleteListing(req));
    }

    private String renderDeleteListingTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/deleteListing.vm", model);
    }
    private String parseDeleteListing(Request req) {
        Map<String, Object> model = new HashMap<>();
        String itemName = req.queryParams("name");


        PreparedStatement preparedStatementDelete = null;
        try {
            preparedStatementDelete = con.prepareStatement("SELECT * FROM item WHERE name = ?");
            preparedStatementDelete.setString(1, itemName);
            ResultSet resultSet = preparedStatementDelete.executeQuery();
            while (resultSet.next()) {
                
            }


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Listing was not found");
        }





        return renderTemplate("velocity/deleteListing.vm", new HashMap());
    }


}
