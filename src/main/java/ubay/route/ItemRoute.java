package ubay.route;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

import spark.Request;
import ubay.model.Item;

import static ubay.database.DatabaseConnection.con;

public class ItemRoute extends TemplateRenderer {

    public ItemRoute() {
        get("/item/template/:id", (req, res) -> renderItemTemplate(req));
    }

    private String renderItemTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("item", getItem(req));
        return renderTemplate("velocity/item.vm", model);
    }

    private Item getItem(Request req) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Item item = null;

        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item WHERE id = ?");
            preparedStatement.setInt(1, Integer.parseInt(req.params(":id")));
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                item = new Item(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("photo"),
                        resultSet.getInt("seller_id"),
                        resultSet.getInt("tag_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

        return item;
    }
}
