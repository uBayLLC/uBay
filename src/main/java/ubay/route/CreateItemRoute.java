package ubay.route;

import spark.Request;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.put;
import static ubay.database.DatabaseConnection.con;

public class CreateItemRoute extends TemplateRenderer {

    public CreateItemRoute() {
        get("/CreateItem-template", (req, res) -> renderLoginTemplate(req));
        put("/CreateItem-data", (req, res) -> createItem(req));
    }

    private String renderLoginTemplate(Request req) {
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

            model.put("name", name);
            model.put("description", description);
            model.put("photolink", photolink);
            model.put("seller_id", seller_id);
            model.put("tag_id", tag_id);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("INSERT INTO item VALUES (NULL,'" +name+  "', '" +description+ "', '" +photolink+  "', '" +seller_id+  "', '" +tag_id+ "')  ");;
        } catch (SQLException exc) {
            System.out.println("Intem cannot be created at this time.  ¯\\_(ツ)_/¯");
        }

        return renderTemplate("velocity/home.vm", new HashMap());
    }
}
