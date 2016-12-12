package ubay.route;

import spark.Request;
import ubay.model.Account;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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
            String firstName = req.queryParams("firstname");
            String lastName = req.queryParams("lastname");
            String address = req.queryParams("address");
            String cardNumber = req.queryParams("cardnumber");

            //Throws exception if any required fields are left blank
            if (email.equalsIgnoreCase("") || password.equalsIgnoreCase("") || firstName.equalsIgnoreCase("") || lastName.equalsIgnoreCase("") || address.equalsIgnoreCase("") ) {
                throw new IOException(); }

            if (cardNumber.equalsIgnoreCase("")) {
                cardNumber = "0"; }

            //Add account to database
            Statement stmt = con.createStatement();
            stmt.execute("INSERT INTO account VALUES (NULL, '"+email+"', '"+password+"', '"+firstName+"', '"+lastName+"', '"+address+"', '"+cardNumber+"')");

            //Create account object
            Account aAccount = new Account(firstName, lastName, email, password, address);
            aAccount.setCard(Integer.parseInt(cardNumber));

            sendTo = renderTemplate("velocity/home.vm", model); }

        //Catches exception if any required fields are left blank
        catch (IOException ioe) {
            model.put("var", "All items with * are required.");
            sendTo = renderTemplate("velocity/createAccount.vm", model); }

        //Catches exception if something goes wrong while adding account to database
        catch (SQLException sqle) {
            model.put("var", "An error occured please try again.");
            sendTo = renderTemplate("velocity/createAccount.vm", model); }

        return sendTo; }

}