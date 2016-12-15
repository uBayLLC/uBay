package ubay.route;

import spark.Request;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

public class DeleteListingRoute extends TemplateRenderer {

    public DeleteListingRoute() {
        get("/item/delete-template", (req, res) -> renderDeleteListingTemplate(req));
        post("/item/data", (req, res) -> parsecreateItemListing(req));
    }

    private String renderDeleteListingTemplate(Request req) {
        Map<String, Object> model = new HashMap<>();
        return renderTemplate("velocity/createItemListing.vm", model);
    }


}
