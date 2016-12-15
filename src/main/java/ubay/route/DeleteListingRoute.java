package ubay.route;

import spark.Request;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class DeleteListingRoute extends TemplateRenderer {

    public DeleteListingRoute() {
        get("/item/delete-template", (req, res) -> renderDeleteListingTemplate(req));
        post("/item/delete/data", (req, res) -> parseDeleteListing(req));
    }


    private String renderDeleteListingTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/deleteListing.vm", model);
    }
    private String parseDeleteListing(Request req) {
        Map<String, Object> model = new HashMap<>();
        String itemName = req.queryParams("name");

        try {
            PreparedStatement preparedStatementDeleteitem = con.prepareStatement("DELETE FROM item WHERE name = ?");
            preparedStatementDeleteitem.setString(1, itemName);
            int result = preparedStatementDeleteitem.executeUpdate();
            if (result != 0) {
                System.out.println("Success, Item was deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Item was not found");
        }

        return renderTemplate("velocity/deleteListing.vm", new HashMap());
    }


}
