package ubay.route;


import spark.Request;
import ubay.model.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static ubay.database.DatabaseConnection.con;

public class IndexRoute extends TemplateRenderer {

    public IndexRoute() {
        get("/", (req, res) -> renderIndex(req));
    }

    private String renderIndex(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("items",  getItems());
        return renderTemplate("velocity/index.vm", model);
    }

    private List<Item> getItems() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Item> items = new ArrayList<>();


        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                Item item = new Item(
                        resultSet.getInt("item_id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("photo"),
                        resultSet.getInt("seller_id"),
                        resultSet.getInt("tag_id")
                );
                items.add(item);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

        return items;
    }
}
