package ubay.route;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

import spark.Request;

import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class LoginRoute extends TemplateRenderer {

    String outcome;

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

        String email = req.queryParams("email");
        String password = req.queryParams("password");

        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM account WHERE email = '" + email + "'" + " AND " + "password = '" + password + "'");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.getString("card");
            outcome = renderTemplate("velocity/home.vm", model);

        } catch (SQLException exc) {
            exc.printStackTrace();
            model.put("var", "Invalid Login");
            outcome = renderTemplate("velocity/login.vm", model);
        }

        return outcome;
    }

}