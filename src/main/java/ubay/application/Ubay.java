package ubay.application;

import spark.*;
import spark.template.velocity.*;
import ubay.route.LoginRoute;

import java.sql.*;
import java.util.*;
import static spark.Spark.*;

public class Ubay {

    public static Connection con;

    public static void main(String [] args) {

//        try {
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/uBay", "root", "1234");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        // Render main UI
        get("/", (req, res) -> renderGUI(req));

        initializeRoutes();

    }

    public static String parseLogin(Request req) {

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

    public static String renderGUI(Request req) {
        String statusStr = req.queryParams("status");
        Map<String, Object> model = new HashMap<>();
        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/todoList.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    public static String renderLoginTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/login.vm", model);
    }

    public static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

    private static void initializeRoutes() {
        new LoginRoute();
    }
}