package ubay.route;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

import spark.Request;
import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;

public class LoginRoute extends TemplateRenderer {

    public LoginRoute() {
        get("/login/template", (req, res) -> renderLoginTemplate(req));
        post("/login/data", (req, res) -> parseLoginData(req));}

    private String renderLoginTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();

        model.put("var", "hello");
        
        return renderTemplate("velocity/login.vm", model);
    }

    private String parseLoginData(Request req) {
        Map<String, Object> model = new HashMap<>();

        String email = req.queryParams("email");
        String password = req.queryParams("password");

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT card FROM account WHERE email = '" + email + "'" + " AND " + "password = '" + password + "'");
            rs.next();
            String cnum = rs.getString("card");
            System.out.println(cnum);
        } catch (SQLException exc) {
            exc.printStackTrace();
            System.out.println("Invalid Login");
        }

        return renderTemplate("velocity/home.vm", model);
    }

}