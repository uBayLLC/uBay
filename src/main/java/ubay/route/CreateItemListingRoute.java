package ubay.route;

import spark.Request;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.text.*;

import static javax.swing.text.html.HTML.Tag.I;
import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class CreateItemListingRoute extends TemplateRenderer {

    String sendTo;

    public CreateItemListingRoute() {
        get("/item/create-template", (req, res) -> renderCreateItemTemplate(req));
        post("/item/data", (req, res) -> parsecreateItemListing(req)); }

    private String renderCreateItemTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
        return renderTemplate("velocity/createItemListing.vm", model); }

    private String parsecreateItemListing(Request req) {
        Map<String, Object> model = new HashMap<>();

        String itemName = req.queryParams("name");
        String description = req.queryParams("description");
        String photolink = req.queryParams("photolink");
        String startingBid = req.queryParams("startingbid");
        String endDate = req.queryParams("end_date");
        String endTime = req.queryParams("end_time");

        //Create Item Try Catch
        try {

            if (itemName.equalsIgnoreCase("") || description.equalsIgnoreCase("")) {
                throw new IOException(); }

            //Create item
            PreparedStatement preparedStatementItem = con.prepareStatement("INSERT INTO item (name, description, photo, seller_id, tag_id) VALUES (?,?,?,?,?)");
            preparedStatementItem.setString(1, itemName);
            preparedStatementItem.setString(2, description);
            preparedStatementItem.setString(3, photolink);
            preparedStatementItem.setInt(4, 1);
            preparedStatementItem.setInt(5, 1);
            int result = preparedStatementItem.executeUpdate();
            if (result != 0) {
                System.out.println("Created Item");
                sendTo = renderTemplate("velocity/myAccount.vm", model); }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            model.put("error", "All items with * are required.");
            sendTo = renderTemplate("velocity/createItemListing.vm", model); }

        catch (SQLException e) {
            e.printStackTrace();
            model.put("error", "An error occured please try again.");
            System.out.println("Couldn't Create Item, SQL");
            sendTo = renderTemplate("velocity/createItemListing.vm", model); }


        //Create Auction Try Catch
        try {

            if (startingBid.equalsIgnoreCase("") || endDate.equalsIgnoreCase("") || endTime.equalsIgnoreCase("")) {
                throw new IOException(); }

            String [] dateSplit = endDate.split("/");

            String month = dateSplit[0];
            String day = dateSplit[1];
            String year = dateSplit[2];

            String formattedDateTime = (year+"-"+month+"-"+day+" "+endTime);

            int itemID = 0;
            PreparedStatement preparedStatementFindItem = con.prepareStatement("SELECT item_id FROM item WHERE name = ? ");
            preparedStatementFindItem.setString(1, itemName);
            ResultSet resultSet = preparedStatementFindItem.executeQuery();
            while (resultSet.next()) {
                itemID = resultSet.getInt("item_id"); }

            //Create Auction
            PreparedStatement preparedStatementListing = con.prepareStatement("INSERT  INTO auction (item_id, starting_price, bid_id, end_datetime) VALUES (?,?,?,?)");
            preparedStatementListing.setInt(1, itemID);
            preparedStatementListing.setInt(2, Integer.parseInt(startingBid));
            preparedStatementListing.setInt(3, 1);
            preparedStatementListing.setString(4, formattedDateTime);
            int result = preparedStatementListing.executeUpdate();
            if (result != 0) {
                System.out.println("Created Auction");
                sendTo = renderTemplate("velocity/myAccount.vm", model);} }

        catch (IOException ioe1) {
            ioe1.printStackTrace();
            deleteItem(model);
            model.put("error", "All items with * are required.");
            sendTo = renderTemplate("velocity/createItemListing.vm", model); }

        catch (InputMismatchException ime) {
            ime.printStackTrace();
            deleteItem(model);
            model.put("error", "Date and/or time entered are in the past.");
            sendTo = renderTemplate("velocity/createItemListing.vm", model); }

        catch (SQLException e1) {
            e1.printStackTrace();
            deleteItem(model);
            model.put("error", "An error occured please try again.");
            System.out.println("Couldn't Create Auction, SQL");
            sendTo = renderTemplate("velocity/createItemListing.vm", model); }

        return sendTo; }

    private void deleteItem(Map<String, Object> model) {
        try {
            PreparedStatement findItemCreated = con.prepareStatement("SELECT max(item_id) FROM item");
            ResultSet rs = findItemCreated.executeQuery();
            rs.next();
            PreparedStatement deleteItemCreated = con.prepareStatement("DELETE FROM item WHERE item_id = "+Integer.parseInt(rs.getString(1)));
            deleteItemCreated.executeUpdate();
        } catch (SQLException sql2) {
            sql2.printStackTrace();
            model.put("error", "error");
            sendTo = renderTemplate("velocity/createItemListing.vm", model); } }
}