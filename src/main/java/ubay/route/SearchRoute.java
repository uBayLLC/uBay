package ubay.route;
import spark.Request;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

import static spark.Spark.post;
import static ubay.database.DatabaseConnection.con;
import static ubay.route.TemplateRenderer.renderTemplate;

public class SearchRoute {


    public SearchRoute()
    {
        post("/search/data", (req, res) -> parseSearchData(req));
    }

    private String parseSearchData(Request req)
    {
        Map<String, Object> model = new HashMap<>();
        System.out.println("Hello");

        String itemName = req.queryParams("");
        System.out.println(itemName);

        try {
            System.out.println("This is my try clause");
            Statement statement = con.createStatement();
            ResultSet result = statement.executeQuery("Select * From Item Where name like '" + itemName + "'");

            ResultSetMetaData column = result.getMetaData();

            int columnsNumber = column.getColumnCount();
            while (result.next()) {
               for (int i = 1; i <= columnsNumber; i++) {
                 if (i > 1) System.out.print(",  ");
               String columnValue = result.getString(i);
           System.out.print(columnValue + " " + column.getColumnName(i));
           }
            System.out.println("");
            }
        }
     catch (SQLException exc) {
        exc.printStackTrace();
        System.out.println("Invalid Search");
    }
    return renderTemplate("velocity/home.vm", model);
    }
}
