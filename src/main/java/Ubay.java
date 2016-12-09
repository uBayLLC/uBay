import spark.*;
import spark.template.velocity.*;

import java.sql.*;
import java.util.*;
import static spark.Spark.*;

public class Ubay {

    public static Connection con;

    public static void main(String [] args) {

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/uBay", "root", "1234");

            exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
            staticFiles.location("/public");
            port(9999);

            // Render main UI
            get("/", (req, res) -> renderGUI(req));
            get("/login", (req, res) -> renderLoginTemplate(req));
            get("/createAccount", (req, res) -> renderCreatAccountTemplate(req));
            put("/login2", (req, res) -> parseLogin(req));
            put("/createAccount2", (req, res) -> parseCreateAccount(req));

        } catch (SQLException exc) {
            System.out.println("Couldn't Connect To DB"); }
    }

    private static String parseCreateAccount(Request req) {

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

    private static String parseLogin(Request req) {

        String email = req.queryParams("email");
        String password = req.queryParams("password");

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT card FROM account WHERE email = '"+email+"'" +" AND "+ "password = '"+password+"'");
            rs.next();
            String cnum = rs.getString("card");
            System.out.println(cnum);
        } catch (SQLException exc) {
            System.out.println("Invalid Login"); }

        return renderTemplate("velocity/home.vm", new HashMap()); }

    private static String renderGUI(Request req) {
        String statusStr = req.queryParams("status");
        Map<String, Object> model = new HashMap<>();
        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/todoList.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderLoginTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/login.vm", model);
    }

    private static String renderCreatAccountTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/createAccount.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}