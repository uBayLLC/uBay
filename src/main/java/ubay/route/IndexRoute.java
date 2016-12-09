package ubay.route;


import spark.Request;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

public class IndexRoute extends TemplateRenderer {

    public IndexRoute() {
        get("/", (req, res) -> renderGUI(req));
    }

    private String renderGUI(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/index.vm", model);
    }

}
