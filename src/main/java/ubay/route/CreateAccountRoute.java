package ubay.route;

import spark.Request;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.put;
import static ubay.database.DatabaseConnection.con;

public class CreateAccountRoute extends TemplateRenderer {

    public CreateAccountRoute() {
        get("/account/create/template", (req, res) -> renderCreateAccountTemplate(req));
        put("/account/data", (req, res) -> parseCreateAccountData(req));
    }

    private static String renderCreateAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/createAccount.vm", model);
    }

    private static String parseCreateAccountData(Request req) {

        String email = req.queryParams("email");
        String password = req.queryParams("password");
        String firstName = req.queryParams("firstname");
        String lastName = req.queryParams("lastname");
        String address = req.queryParams("address");
        String cardNumber = req.queryParams("cardnumber");

        try {
            Statement stmt = con.createStatement();
            stmt.execute("INSERT INTO account VALUES (NULL, '"+email+"', '"+password+"', '"+firstName+"', '"+lastName+"', '"+address+"', '"+cardNumber+"')");
            System.out.println("Suscess");
        } catch (SQLException exc) {
            System.out.println("Couldn't Create Account"); }

        return renderTemplate("velocity/home.vm", new HashMap()); }

}
