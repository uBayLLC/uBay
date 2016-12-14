package ubay.route;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.text.*;
import java.util.Date;

import static ubay.database.DatabaseConnection.con;

public class AuctionRoute {

    public AuctionRoute() {} {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();

        try {

        PreparedStatement stmt = con.prepareStatement("SELECT bid_amount, end_datetime FROM bid, auction WHERE bid.bid_id = auction.bid_id");
        ResultSet rs = stmt.executeQuery();
        rs.next();

        System.out.println(rs.getTimestamp("end_datetime"));

        while (rs.next()) {
            int result = dateFormat.format(dt).compareTo(rs.getString("end_datetime"));

            //-1 = right > left
            //0 = equal
            //1 = left > right]

            if (result == 1) {
                FindPercentageToBeTaken(Integer.parseInt(rs.getString("bid_amount"))); } }

        } catch(SQLException sqle) {
            sqle.printStackTrace(); }
    }

    private double FindPercentageToBeTaken(int highestBid) {

        double result = 0;

        if (highestBid <= 10) {
            result = CalculateAmountToHold(highestBid, .005); }
        if (highestBid >= 10.01 && highestBid <= 30) {
            result = CalculateAmountToHold(highestBid, .01); }
        if (highestBid >= 30.01 && highestBid <= 60) {
            result = CalculateAmountToHold(highestBid, .025); }
        if (highestBid >= 60.01 && highestBid <= 100) {
            result = CalculateAmountToHold(highestBid, .07); }
        if (highestBid >= 100.01 && highestBid <= 300) {
            result = CalculateAmountToHold(highestBid, .085); }
        if (highestBid >= 300.01 && highestBid <= 700) {
            result = CalculateAmountToHold(highestBid, .095); }
        if (highestBid >= 700.01 && highestBid <= 1000) {
            result = CalculateAmountToHold(highestBid, .11); }
        if (highestBid >= 1000.01 && highestBid <= 5000) {
            result = CalculateAmountToHold(highestBid, .12); }
        if (highestBid >=  5000.01) {
            result = CalculateAmountToHold(highestBid, .15); }

    return result; }

    private double CalculateAmountToHold(int highestBid, double percentageTaken) {
        return (highestBid*percentageTaken); }

}