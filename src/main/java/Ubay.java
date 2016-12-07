import model.Status;
import model.TodoDao;
import spark.*;
import spark.template.velocity.*;
import java.util.*;
import static spark.Spark.*;

public class Ubay {

    public static void main(String [] args) {

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