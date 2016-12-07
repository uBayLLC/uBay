import model.Status;
import model.TodoDao;
import spark.*;
import spark.template.velocity.*;
import java.util.*;
import static spark.Spark.*;


/*
        //Connect to DB
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/uBay", "root", "1234");

        //Execute Query
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT first_name, last_name FROM account");

        //Get Query Results
        while (rs.next()) {
            //Retrieve by column name
            String FN = rs.getString("first_name");
            String LN = rs.getString("last_name");

            //Display values
            System.out.println(", First: " + FN);
            System.out.println(", Last: " + LN);
            System.out.println();
 */
















public class Ubay {

    public static void main(String [] args) {

        //Connect to DB
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/uBay", "root", "1234");

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        // Render main UI
        get("/", (req, res) -> renderGUI(req));
        get("/login", (req, res) -> renderLoginTemplate(req));
        put("/login2", (req, res) -> parseLogin(req)); }


    private static String parseLogin(Request req) {

        String email = req.queryParams("email");
        String password = req.queryParams("password");

        System.out.println(email);
        System.out.print(password);

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

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}