package ubay.route;

import spark.Request;
import ubay.model.Account;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class CreateAccountRoute extends TemplateRenderer {

    private String sendTo;

    public CreateAccountRoute() {
        get("/account/create/template", (req, res) -> renderCreateAccountTemplate(req));
        post("/account/create/data", (req, res) -> parseCreateAccountData(req)); }

    private String renderCreateAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("var", "");
        return renderTemplate("velocity/createAccount.vm", model);
    }

    private String parseCreateAccountData(Request req) {
        Map<String, Object> model = new HashMap<>();

        try {

            //Pull values from form
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            String confirmPassword = req.queryParams("confirmpassword");
            String firstName = req.queryParams("firstname");
            String lastName = req.queryParams("lastname");
            String address = req.queryParams("address");
            String cardNumber = req.queryParams("cardnumber");

            //Throws exception if any required fields are left blank
            if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("") || firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("") || address.equalsIgnoreCase("") ) {
                throw new IOException(); }

            //Throws exception if the passwords don't match
            if (!password.equals(confirmPassword))  {
                throw new InputMismatchException(); }

            //Sets card number to -1 if user didn't enter one
            if (cardNumber.equalsIgnoreCase("")) {
                cardNumber = "-1"; }

            //Add account to database
            PreparedStatement addAccountToDB = con.prepareStatement("INSERT INTO account VALUES (NULL, '"+email+"', '"+password+"', '"+firstName+"', '"+lastName+"', '"+address+"', '" +cardNumber+ "')");
            addAccountToDB.executeUpdate();

            //Get id of new account
            PreparedStatement getIdFromDB = con.prepareStatement("SELECT account_id FROM account WHERE email='" +email+ "';");
            ResultSet rs = getIdFromDB.executeQuery();
            rs.next();

            //Create account object
            Account.getLoggedInUser().setFirstname(firstName);
            Account.getLoggedInUser().setLastname(lastName);
            Account.getLoggedInUser().setEmail(email);
            Account.getLoggedInUser().setPassword(password);
            Account.getLoggedInUser().setAddress(address);
            Account.getLoggedInUser().setCard(Integer.parseInt(cardNumber));
            Account.getLoggedInUser().setId(rs.getInt("account_id"));

            sendTo = renderTemplate("velocity/home.vm", model); }

        //Catches exception if any required fields are left blank
        catch (IOException ioe) {
            model.put("var", "All items with * are required.");
            sendTo = renderTemplate("velocity/createAccount.vm", model); }

        //Catches exception if passwords don't match
        catch (InputMismatchException ime) {
            model.put("var", "Passwords don't match.");
            sendTo = renderTemplate("velocity/createAccount.vm", model); }

        //Catches exception if something goes wrong while adding account to database
        catch (SQLException sqle) {
            model.put("var", "An error occured please try again.");
            sqle.printStackTrace();
            sendTo = renderTemplate("velocity/createAccount.vm", model); }

        return sendTo; }

}