package ubay.route;

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
import spark.Request;

import ubay.model.Account;
import static ubay.database.DatabaseConnection.con;

public class EditAccountRoute extends TemplateRenderer {

    private String sendTo;

    public EditAccountRoute() {
        get("/account/edit-account/template", (req, res) -> renderMyAccountTemplate(req));
        post("/account/edit-account/data", (req, res) -> parseMyAccountData(req)); }

    private String renderMyAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("var", "");
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

            sendTo = renderTemplate("velocity/home.vm", model); }

        //Catches exception if any required fields are left blank
        catch (IOException ioe) {
            model.put("var", "All items with * are required.");
            populateFormWithCurrentInfo(model);
            sendTo = renderTemplate("velocity/editAccount.vm", model); }

        //Catches exception if passwords don't match
        catch (InputMismatchException ime) {
            model.put("var", "Passwords don't match.");
            populateFormWithCurrentInfo(model);
            sendTo = renderTemplate("velocity/editAccount.vm", model); }

        //Catches exception if something goes wrong while adding account to database
        catch (SQLException sqle) {
            model.put("var", "An error occured please try again.");
            populateFormWithCurrentInfo(model);
            sendTo = renderTemplate("velocity/editAccount.vm", model); }

        return sendTo; }

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

}
