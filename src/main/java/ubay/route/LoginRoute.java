package ubay.route;

import static spark.Spark.*;
import static ubay.application.Ubay.*;

public class LoginRoute {

    public LoginRoute() {
        get("/login", (req, res) -> renderLoginTemplate(req));
        put("/login2", (req, res) -> parseLogin(req));
    }

}
