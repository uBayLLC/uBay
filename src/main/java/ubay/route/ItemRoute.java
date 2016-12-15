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

    private int itemId;

    public ItemRoute() {
        get("/item/template/:id", (req, res) -> renderItemTemplate(req));
        post("/item/bid", (req, res) -> renderItemTemplate(req)); }

    private String renderItemTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();

        model.put("error", "");

        if (req.requestMethod() == "GET" ) {
            itemId = Integer.parseInt(req.params(":id"));
            model.put("bid", getBid());
        } else {
            Map<String, Object> m = parseBid(req);
            model.put("bid", m.get("bid"));
            model.put("error", m.get("error"));
        }

        model.put("item", getItem());
        model.put("auction", getAuction());
        return renderTemplate("velocity/item.vm", model);
    }

    private Map<String, Object> parseBid(Request req) {
        Map<String, Object> bidData = new HashMap<>();
        Bid bid = null;
        int newBid = Integer.parseInt(req.queryParams("newbid"));

        try {PreparedStatement getBidFromDB = con.prepareStatement("SELECT bid_amount FROM bid JOIN auction ON bid.bid_id = auction.bid_id WHERE auction.item_id = ? ORDER BY bid_amount ASC");
            getBidFromDB.setInt(1, itemId);
            ResultSet bidsFromDB = getBidFromDB.executeQuery();

            bidsFromDB.next();

            if (newBid <= bidsFromDB.getInt("bid_amount")) {
                throw new IllegalArgumentException(); }

            PreparedStatement sendBidToDB = con.prepareStatement("INSERT INTO bid VALUES (NULL, "+Account.getLoggedInUser().getId()+", "+newBid+");");
            sendBidToDB.executeUpdate();
            PreparedStatement updateAuctionsBidID = con.prepareStatement("UPDATE auction SET auction.bid_id = (SELECT max(bid_id) FROM bid) WHERE auction.item_id =" +itemId+";");
            updateAuctionsBidID.executeUpdate();

            bid = new Bid(
                    Account.getLoggedInUser(),
                    newBid);

            bidData.put("error", "");
            bidData.put("bid", bid);
        }

        catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            bidData.put("error", "Bid must be higher then the current highest bid.");
            bidData.put("bid", getBid()); }

        catch (SQLException sqle) {
            sqle.printStackTrace();
            bidData.put("error", "An error occurred please try again."); }

        return bidData; }

    private Bid getBid() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Bid bid = null;

        try {
            preparedStatement = con.prepareStatement("SELECT bid_amount FROM bid JOIN auction ON bid.bid_id = auction.bid_id WHERE auction.item_id = " + itemId);
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

    private Auction getAuction() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Auction auction = null;
        Item item = null;
        Bid bid = null;

        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item JOIN auction ON item.item_id = auction.item_id JOIN bid ON auction.bid_id = bid.bid_id WHERE item.item_id = " + itemId);
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
                        bid); }

        } catch (SQLException e) {
            e.printStackTrace(); }

        finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); } } }

        return auction; }

    private Item getItem() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Item item = null;

        try {
            preparedStatement = con.prepareStatement("SELECT * FROM item WHERE item_id = " + itemId);
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