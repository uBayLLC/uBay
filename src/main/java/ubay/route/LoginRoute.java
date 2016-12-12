package ubay.route;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import spark.Request;

import ubay.model.Account;
import static ubay.database.DatabaseConnection.con;

public class LoginRoute extends TemplateRenderer {

    private String sendTo;

    public LoginRoute() {
        get("/login/template", (req, res) -> renderLoginTemplate(req));
        post("/login/data", (req, res) -> parseLoginData(req));}

    private String renderLoginTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        model.put("var", "");
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

            //Creates account object
            Account aAccount = new Account(rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("password"), rs.getString("address"));
            aAccount.setCard(rs.getInt("card"));

            sendTo = renderTemplate("velocity/home.vm", model); }

        //Catches exception if values don't match up with an existing account
        catch (SQLException exc) {
            exc.printStackTrace();
            model.put("var", "Invalid Login");
            sendTo = renderTemplate("velocity/login.vm", model);
        }

        return sendTo;
    }

}