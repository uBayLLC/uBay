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
        String filter = req.queryParams("filter");
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        List<Item> items = new ArrayList<>();
        try {

            if (!itemName.equalsIgnoreCase("") && !filter.equalsIgnoreCase("")) {
                preparedStatement = con.prepareStatement("SELECT item.item_id, item.name, item.description, item.photo, item.seller_id, item.tag_id FROM item JOIN tag ON tag.tag_id = item.tag_id WHERE tag.name LIKE '" + filter + "%' AND WHERE item.name = '%" + itemName + "%'");
                result = preparedStatement.executeQuery();
            } else if(!itemName.equalsIgnoreCase("")){
                preparedStatement = con.prepareStatement("SELECT * FROM item WHERE name LIKE '%" + itemName +"%'");
                result = preparedStatement.executeQuery();
            } else if(!filter.equalsIgnoreCase("")) {
                preparedStatement = con.prepareStatement("SELECT item.item_id, item.name, item.description, item.photo, item.seller_id, item.tag_id FROM item JOIN tag ON tag.tag_id = item.tag_id WHERE tag.name LIKE '%" + filter + "%'");
                result = preparedStatement.executeQuery();
            }

            while(result.next()) {
                Item item = new Item(
                        result.getInt("item_id"),
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
