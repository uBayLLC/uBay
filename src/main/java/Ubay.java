import model.Status;
import model.TodoDao;
import spark.*;
import spark.template.velocity.*;
import java.util.*;
import static spark.Spark.*;
import java.sql.*;

/**
 * This class contains exactly the same functionality as TodoList,
 * but it's following normal Spark conventions more closely.
 */
public class Ubay {

    public static void main(String[] args) throws SQLException {

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
        }
    }
/*
    private static String renderTodos(Request req) {
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
        return renderTemplate("velocity/index2.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }
    */

}