package ubay.route;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

import spark.Request;
import ubay.model.Bid;
import ubay.model.Item;
import ubay.model.Auction;
import ubay.model.Account;

import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class ItemRoute extends TemplateRenderer {

    public ItemRoute() {
        get("/item/template/:id", (req, res) -> renderItemTemplate(req));
        post("/item/data", (req, res) -> parseBid(req)); }

    private String renderItemTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
        model.put("item", getItem(req));
        model.put("bid", getBid(req));
        model.put("auction", getAuction(req));
        return renderTemplate("velocity/item.vm", model);
    }

    private String parseBid(Request req) {
        Map<String, Object> model = new HashMap<>();
        Bid bid = null;
        int newBid = Integer.parseInt(req.queryParams("newbid"));

        try {PreparedStatement getBidFromDB = con.prepareStatement("SELECT bid_amount FROM bid JOIN auction ON bid.bid_id = auction.bid_id");
            ResultSet bidsFromDB = getBidFromDB.executeQuery();

            while (bidsFromDB.next()) {

                if (newBid <= bidsFromDB.getInt("bid_amount")) {
                    throw new IllegalArgumentException(); }

                bid = new Bid(
                        Account.getLoggedInUser(),
                        newBid); }
        }

        catch(IllegalArgumentException iae) {
            iae.printStackTrace();
            model.put("error", "Bid must be higher then the current highest bid."); }

        catch (SQLException sqle) {
            sqle.printStackTrace();
            model.put("error", "An error occured please try again."); }

        return renderTemplate("velocity/item.vm", model); }

    private Bid getBid(Request req) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Bid bid = null;

        try {
            preparedStatement = con.prepareStatement("SELECT bid_amount FROM bid JOIN auction ON bid.bid_id = auction.bid_id WHERE auction.item_id = " + req.params(":id"));
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                bid = new Bid(
                        Account.getLoggedInUser(),
                        resultSet.getInt("bid_amount")); }

        } catch (SQLException e) {
            e.printStackTrace(); }

        finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); } } }

        return bid; }

    private Auction getAuction(Request req) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Auction auction = null;
        Item item = null;
        Bid bid = null;

        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item JOIN auction ON item.item_id = auction.item_id JOIN bid ON auction.bid_id = bid.bid_id WHERE item.item_id = " + req.params(":id"));
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                item = new Item(
                        resultSet.getInt("item_id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("photo"),
                        resultSet.getInt("seller_id"),
                        resultSet.getInt("tag_id"));

                bid = new Bid(
                        Account.getLoggedInUser(),
                        resultSet.getInt("bid_amount"));

                auction = new Auction(
                        item,
                        resultSet.getInt("starting_price"),
                        resultSet.getTimestamp("end_datetime"),
                        resultSet.getInt("buy_out_price"),
                        bid); }

        } catch (SQLException e) {
            e.printStackTrace(); }

        finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); } } }

        return auction; }

    private Item getItem(Request req) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Item item = null;

        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item WHERE item_id = " + req.params(":id"));
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                item = new Item(
                        resultSet.getInt("item_id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("photo"),
                        resultSet.getInt("seller_id"),
                        resultSet.getInt("tag_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace(); }

        finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); } } }

        return item;
    }
}