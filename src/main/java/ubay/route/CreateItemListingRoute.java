package ubay.route;

import spark.Request;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class CreateItemListingRoute extends TemplateRenderer {

    public CreateItemListingRoute() {
        get("/item/create-template", (req, res) -> renderCreateItemTemplate(req));
        post("/item/data", (req, res) -> parsecreateItemListing(req));
    }

    private String renderCreateItemTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/createItemListing.vm", model);
    }


    private String parsecreateItemListing(Request req) {
        Map<String, Object> model = new HashMap<>();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String itemName = req.queryParams("name");
        String description = req.queryParams("description");
        String photolink = req.queryParams("photolink");
        String bid = req.queryParams("bid");
        String end_datetime = req.queryParams("end_datetime");
        String seller_id = req.queryParams("seller_id");
        String bid_id = req.queryParams("bid_id");
       /* String soulRadio = req.queryParams("souls");
        String peopleRadio = req.queryParams("people");
        String techRadio = req.queryParams("tech");
        String toyRadio = req.queryParams("toys");
        String servRadio = req.queryParams("services");
        String tag_id = " "; */

        try {

          /*  if (soulRadio.equals("on")) {
                tag_id = "1";
            } else if (peopleRadio.equals("on")) {
                tag_id = "2";
            } else if (techRadio.equals("on")) {
                tag_id = "3";
            } else if (toyRadio.equals("on")) {
                tag_id = "4";
            } else if (servRadio.equals("on")) {
                tag_id = "5";
            } else {
                tag_id = "0";
            } */

            //Create item
            PreparedStatement preparedStatementItem = con.prepareStatement("INSERT INTO item (name, description, photo, seller_id, tag_id) VALUES (?,?,?,?,?)");
            preparedStatementItem.setString(1, itemName);
            preparedStatementItem.setString(2, description);
            preparedStatementItem.setString(3, photolink);
            preparedStatementItem.setInt(4, 1);
            preparedStatementItem.setInt(5, 1);
            int result = preparedStatementItem.executeUpdate();
            if (result != 0) {
                System.out.println("Success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Item cannot be created at this time.  ¯\\_(ツ)_/¯");
        }


        try {
            int itemID = 0;
            PreparedStatement preparedStatementFindItem = con.prepareStatement("SELECT item_id FROM item WHERE name = ''?'' ");
            preparedStatementFindItem.setString(1, itemName);
            ResultSet resultSet = preparedStatementFindItem.executeQuery();
            if (resultSet.next()) {
                itemID = resultSet.getInt("item_id");
            }

            //Create Auction
            PreparedStatement preparedStatementListing = con.prepareStatement("INSERT  INTO auction (item_id, starting_price, bid_id, end_datetime) VALUES (?,?,?,?)");
            preparedStatementListing.setInt(1, itemID);
            preparedStatementListing.setInt(2, Integer.parseInt(bid));
            preparedStatementListing.setInt(3, 1);
            preparedStatementListing.setString(4, end_datetime);
            int result = preparedStatementListing.executeUpdate();
            if (result != 0) {
                System.out.println("Success");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            System.out.println("Listing cannot be created at this time.  ¯\\_(ツ)_/¯");
        }


        return renderTemplate("velocity/createItemListing.vm", new HashMap());
    }
}

