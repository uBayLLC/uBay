package ubay.route;

import spark.Request;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class CreateItemRoute extends TemplateRenderer {

    public CreateItemRoute() {
        get("/item/create-template", (req, res) -> renderCreateItemTemplate(req));
        post("/item/data", (req, res) -> createItem(req));
    }

    private String renderCreateItemTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/createItem.vm", model);
    }


    private String createItem(Request req) {
        Map<String, Object> model = new HashMap<>();

        String name = req.queryParams("name");
        String description = req.queryParams("description");
        String photolink = req.queryParams("photolink");
        String seller_id = req.queryParams("seller_id");
        String tag_id = req.queryParams("tag_id");

        try {

//            model.put("name", name);
//            model.put("description", description);
//            model.put("photolink", photolink);
//            model.put("seller_id", seller_id);
//            model.put("tag_id", tag_id);
            PreparedStatement preparedStatement = con.prepareStatement("INSERT  INTO item VALUES (NULL, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, photolink);
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, Integer.parseInt(tag_id));
            int result = preparedStatement.executeUpdate();
            if (result != 0) {
                System.out.println("Success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Item cannot be created at this time.  ¯\\_(ツ)_/¯");
        }

        return renderTemplate("velocity/home.vm", new HashMap());
    }
}
