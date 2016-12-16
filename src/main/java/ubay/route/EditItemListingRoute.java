package ubay.route;

import spark.Request;
import ubay.model.Account;
import ubay.model.Auction;
import ubay.model.Bid;
import ubay.model.Item;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class EditItemListingRoute extends TemplateRenderer {

    int itemId;
    Auction auction;

    public EditItemListingRoute() {
        get("/item/edit/template", (req, res) -> renderAccountItemListTemplate());
        get("/item/edit/:id", (req, res) -> renderEditItemListingTemplate(req));
        post("/item/edit/data", (req, res) -> parseEditItemListing(req));
    }

    private String renderAccountItemListTemplate() {
        Map<String, Object> model = new HashMap<>();
        model.put("items",  getAccountItems());
        model.put("loggedIn", true);
        return renderTemplate("velocity/home.vm", model);
    }

    private List<Item> getAccountItems() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Item> items = new ArrayList<>();

        try {
            preparedStatement = con.prepareStatement("SELECT item.item_id, item.name, item.description, item.photo, item.seller_id, item.tag_id FROM item JOIN account ON item.seller_id = account.account_id WHERE account_id = ?");
            preparedStatement.setInt(1, Account.getLoggedInUser().getId());
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

    private String renderEditItemListingTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSet resultSetBid = null;
        itemId = Integer.parseInt(req.params(":id"));

        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item JOIN auction ON item.item_id = auction.item_id WHERE item.item_id = ?");
            preparedStatement.setInt(1, itemId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Item item = new Item(
                    resultSet.getInt("item_id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("photo"),
                    resultSet.getInt("seller_id"),
                    resultSet.getInt("tag_id")
            );

            preparedStatement = con.prepareStatement("SELECT * FROM auction JOIN bid ON auction.bid_id = bid.bid_id JOIN account ON bid.buyer_id = account.account_id WHERE auction.bid_id = ?");
            preparedStatement.setInt(1, resultSet.getInt("bid_id"));
            resultSetBid = preparedStatement.executeQuery();
            resultSetBid.next();

            auction = new Auction(
                    item,
                    resultSet.getInt("starting_price"),
                    resultSet.getTimestamp("end_datetime"),
                    new Bid(
                            new Account(
                                    resultSetBid.getString("first_name"),
                                    resultSetBid.getString("last_name"),
                                    resultSetBid.getString("email"),
                                    resultSetBid.getString("password"),
                                    resultSetBid.getString("address"),
                                    resultSetBid.getInt("account_id")
                            ),
                            resultSetBid.getInt("bid_id")
                    )
            );

            model.put("auction", auction);
            model.put("error", "");

        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (resultSetBid != null) {
                try { resultSetBid.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

        return renderTemplate("velocity/editItem.vm", model);
    }

    private String parseEditItemListing(Request req) {
        Map<String, Object> model = new HashMap<>();
        String template = null;

        try {
            //Pull values from form
            String name = req.queryParams("name");
            String description = req.queryParams("description");
            String photoUrl = req.queryParams("photolink");
            String startingBid = req.queryParams("startingbid");
            String endDatetime = req.queryParams("end_datetime");
            String endDate = req.queryParams("end_date");
            String endTime = req.queryParams("end_time");

            //Throws exception if any required fields are left blank
            if (name.equalsIgnoreCase("") || description.equalsIgnoreCase("") || startingBid.equalsIgnoreCase("") || endDatetime.equalsIgnoreCase("")) {
                throw new IOException(); }

            //Update account info on database
            PreparedStatement stmt = con.prepareStatement("UPDATE item SET name = ?, description = ?,  photo = ? WHERE item_id = ?");
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, photoUrl);
            stmt.setInt(4, itemId);
            stmt.executeUpdate();

            stmt = con.prepareStatement("UPDATE auction SET starting_price = ?, end_datetime = ? WHERE item_id = ?");
            stmt.setString(1, startingBid);
            stmt.setString(2, endDatetime);
            stmt.setInt(3, itemId);
            stmt.executeUpdate();

            template = renderTemplate("velocity/home.vm", model); }

        //Catches exception if any required fields are left blank
        catch (IOException ioe) {
            model.put("var", "All items with * are required.");
            model.put("auction", auction);
            template = renderTemplate("velocity/editItem.vm", model); }

            //Catches exception if something goes wrong while adding account to database
        catch (SQLException sqle) {
            model.put("var", "An error occured please try again.");
            model.put("auction", auction);
            template = renderTemplate("velocity/editItem.vm", model); }

        return template; }

}




