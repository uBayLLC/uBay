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

public class EditAccountRoute extends TemplateRenderer {

    private String sendTo;

    public EditAccountRoute() {
        get("/account/edit-account/template", (req, res) -> renderMyAccountTemplate(req));
        post("/account/edit-account/data", (req, res) -> parseMyAccountData(req));}

    private String renderMyAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/editAccount.vm", model); }

    private String parseMyAccountData(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/myAccount.vm", model); }

}