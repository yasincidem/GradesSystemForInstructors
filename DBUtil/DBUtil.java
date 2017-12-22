package DBUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by yasin_000 on 19.12.2017.
 */
public class DBUtil {
    //Declare JDBC Driver
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    //Connection String
    private static final String connStr = "jdbc:mysql://localhost:3306/gradingsystemdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";

    //Connection
    private static Connection conn = null;

    private static String user = "root";
    private static String password = "";

    public static Connection connectDB() {
        try {Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(connStr, user, password);
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Driver");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Something wrong with Connection");
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
