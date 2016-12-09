package ubay.route;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.Map;

public abstract class TemplateRenderer {

    protected static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}
