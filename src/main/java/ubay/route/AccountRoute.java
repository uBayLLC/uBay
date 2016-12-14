package ubay.route;

import spark.Request;
import ubay.model.Account;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static ubay.database.DatabaseConnection.con;

public class AccountRoute extends TemplateRenderer {

    private String template;

    public AccountRoute() {
        get("/account/login/template", (req, res) -> renderLoginTemplate(req));
        post("/account/login/data", (req, res) -> parseLoginData(req));

        get("/account/create/template", (req, res) -> renderCreateAccountTemplate(req));
        post("/account/create/data", (req, res) -> parseCreateAccountData(req));

        get("/account/template", (req, res) -> renderMyAccountTemplate(req));
        put("/account/data", (req, res) -> parseMyAccountData(req));

        get("/account/buttons/template", (req, res) -> renderAccountButtonsTemplate(req));
    }

    private String renderAccountButtonsTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/myAccount.vm", model); }

    private String renderMyAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
        populateFormWithCurrentInfo(model);
        return renderTemplate("velocity/editAccount.vm", model); }

    private String parseMyAccountData(Request req) {
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
            if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("") || firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("") || address.equalsIgnoreCase("")) {
                throw new IOException(); }

            //Throws exception if the passwords don't match
            if (!password.equals(confirmPassword)) {
                throw new InputMismatchException(); }

            //Sets card number to -1 if user didn't enter one
            if (cardNumber.equalsIgnoreCase("")) {
                cardNumber = "-1"; }

            //Update account info on database
            PreparedStatement stmt = con.prepareStatement("UPDATE account SET email='" + email + "', password='" + password + "', first_name='" + firstName + "', last_name='" + lastName + "', address='" + address + "', card='" + cardNumber + "' WHERE id='" +Account.getLoggedInUser().getId()+ "';");
            stmt.executeUpdate();

            //Update Account Object
            Account.getLoggedInUser().setEmail(email);
            Account.getLoggedInUser().setPassword(password);
            Account.getLoggedInUser().setFirstname(firstName);
            Account.getLoggedInUser().setLastname(lastName);
            Account.getLoggedInUser().setAddress(address);
            Account.getLoggedInUser().setCard(Integer.parseInt(cardNumber));

            template = renderTemplate("velocity/home.vm", model); }

        //Catches exception if any required fields are left blank
        catch (IOException ioe) {
            model.put("error", "All items with * are required.");
            populateFormWithCurrentInfo(model);
            template = renderTemplate("velocity/editAccount.vm", model); }

        //Catches exception if passwords don't match
        catch (InputMismatchException ime) {
            model.put("error", "Passwords don't match.");
            populateFormWithCurrentInfo(model);
            template = renderTemplate("velocity/editAccount.vm", model); }

        //Catches exception if something goes wrong while adding account to database
        catch (SQLException sqle) {
            model.put("error", "An error occured please try again.");
            populateFormWithCurrentInfo(model);
            template = renderTemplate("velocity/editAccount.vm", model); }

        return template;
    }

    private void populateFormWithCurrentInfo(Map<String, Object> model) {
        model.put("email", Account.getLoggedInUser().getEmail());
        model.put("password", Account.getLoggedInUser().getPassword());
        model.put("confirmpassword", Account.getLoggedInUser().getPassword());
        model.put("firstname", Account.getLoggedInUser().getFirstname());
        model.put("lastname", Account.getLoggedInUser().getLastname());
        model.put("address", Account.getLoggedInUser().getAddress());

        if (Account.getLoggedInUser().getCard() == -1) {
            model.put("cardnumber", ""); }
        else {
            model.put("cardnumber", Account.getLoggedInUser().getCard()); } }

    private String renderCreateAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
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
            PreparedStatement getIdFromDB = con.prepareStatement("SELECT id FROM account WHERE email='" +email+ "';");
            ResultSet rs = getIdFromDB.executeQuery();
            rs.next();

            //Create account object
            Account.getLoggedInUser().setFirstname(firstName);
            Account.getLoggedInUser().setLastname(lastName);
            Account.getLoggedInUser().setEmail(email);
            Account.getLoggedInUser().setPassword(password);
            Account.getLoggedInUser().setAddress(address);
            Account.getLoggedInUser().setCard(Integer.parseInt(cardNumber));
            Account.getLoggedInUser().setId(rs.getInt("id"));

            template = renderTemplate("velocity/home.vm", model); }

        //Catches exception if any required fields are left blank
        catch (IOException ioe) {
            model.put("error", "All items with * are required.");
            template = renderTemplate("velocity/createAccount.vm", model); }

        //Catches exception if passwords don't match
        catch (InputMismatchException ime) {
            model.put("error", "Passwords don't match.");
            template = renderTemplate("velocity/createAccount.vm", model); }

        //Catches exception if something goes wrong while adding account to database
        catch (SQLException sqle) {
            model.put("error", "An error occured please try again.");
            template = renderTemplate("velocity/createAccount.vm", model); }

        return template; }

    private String renderLoginTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
        return renderTemplate("velocity/login.vm", model);
    }

    private String parseLoginData(Request req) {
        Map<String, Object> model = new HashMap<>();

        //Pull values from form
        String email = req.queryParams("email");
        String password = req.queryParams("password");

        try {
            //See if values match up with an existing account
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM account WHERE email = '" + email + "'" + " AND " + "password = '" + password + "'");
            ResultSet rs = stmt.executeQuery();
            rs.next();

            //Create account object
            Account.getLoggedInUser().setFirstname(rs.getString("first_name"));
            Account.getLoggedInUser().setLastname(rs.getString("last_name"));
            Account.getLoggedInUser().setEmail(rs.getString("email"));
            Account.getLoggedInUser().setPassword(rs.getString("password"));
            Account.getLoggedInUser().setAddress(rs.getString("address"));
            Account.getLoggedInUser().setCard(rs.getInt("card"));
            Account.getLoggedInUser().setId(rs.getInt("id"));

            template = renderTemplate("velocity/home.vm", model); }

        //Catches exception if values don't match up with an existing account
        catch (SQLException exc) {
            exc.printStackTrace();
            model.put("error", "Invalid Login");
            template = renderTemplate("velocity/login.vm", model);
        }

        return template;
    }

}
