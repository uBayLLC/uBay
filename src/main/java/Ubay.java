import model.Status;
import model.TodoDao;
import spark.*;
import spark.template.velocity.*;
import java.util.*;
import static spark.Spark.*;

/**
 * This class contains exactly the same functionality as TodoList,
 * but it's following normal Spark conventions more closely.
 */
public class Ubay {

    public static void main(String[] args) {

        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");
        port(9999);

        // Render main UI
        get("/", (req, res) -> renderGUI(req));
        get("/login", (req, res) -> renderLogin(req));

    }

    private static String renderGUI(Request req) {
        String statusStr = req.queryParams("status");
        Map<String, Object> model = new HashMap<>();
        model.put("todos", TodoDao.ofStatus(statusStr));
        model.put("filter", Optional.ofNullable(statusStr).orElse(""));
        model.put("activeCount", TodoDao.ofStatus(Status.ACTIVE).size());
        model.put("anyCompleteTodos", TodoDao.ofStatus(Status.COMPLETE).size() > 0);
        model.put("allComplete", TodoDao.all().size() == TodoDao.ofStatus(Status.COMPLETE).size());
        model.put("status", Optional.ofNullable(statusStr).orElse(""));
        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/todoList.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderLogin(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/login.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}