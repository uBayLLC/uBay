package ubay.route;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.put;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.redirect;
import static ubay.application.Ubay.*;

public class LoginRoute extends TemplateRenderer {

    public LoginRoute() {
        get("/login-template", (req, res) -> renderLoginTemplate(req));
        put("/login-data", (req, res) -> parseLoginData(req, res));
    }

    private String renderLoginTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/login.vm", model);
    }

    private String parseLoginData(Request req, Response res) {

        String email = req.queryParams("email");
        String password = req.queryParams("password");

        String outcome = "";
        //outcome = renderTemplate("velocity/home.vm", new HashMap());

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT card FROM account WHERE email = '" + email + "'" + " AND " + "password = '" + password + "'");
            rs.next();
            String cnum = rs.getString("card");
            System.out.println(cnum);
            //res.responseText();
            outcome = renderTemplate("velocity/home.vm", new HashMap());
        } catch (SQLException exc) {
            System.out.println("Invalid Login");
            outcome = renderTemplate("velocity/navbar.vm", new HashMap());
        }

        return outcome;
    }

}
