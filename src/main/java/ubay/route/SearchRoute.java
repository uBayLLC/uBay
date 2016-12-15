package ubay.route;
import spark.Request;
import ubay.model.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;
import static ubay.route.TemplateRenderer.renderTemplate;

public class SearchRoute {


    public SearchRoute()
    {
        post("/search/data", (req, res) -> parseSearchData(req));
    }

    private String parseSearchData(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("items", getItems(req));
        return renderTemplate("velocity/home.vm", model);

    }

    private List<Item> getItems(Request req){

        String itemName = req.queryParams("search");
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        List<Item> items = new ArrayList<>();
        try {
          preparedStatement = con.prepareStatement("Select * From Item Where name like '" + itemName +"%';");
          result = preparedStatement.executeQuery();




            while(result.next()) {
                Item item = new Item(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("description"),
                        result.getString("photo"),
                        result.getInt("seller_id"),
                        result.getInt("tag_id")
                );
                items.add(item);

            }
        } catch(SQLException e) {

            e.printStackTrace();
        } finally {
            if (result != null) {
                try { result.close(); }
                catch (SQLException e) { e.printStackTrace(); }
            }
            if (preparedStatement != null) {
                try { preparedStatement.close(); }
                catch (SQLException e) { e.printStackTrace(); }
            }
        }

        return items;
    }
}
