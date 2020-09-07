import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnUtils {
    public static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
        String hostName = "localhost";

        String dbName = "";
        String userName = "";
        String password = "";

        return getMySQLConnection(hostName, dbName, userName, password);
    }

    public static Connection getMySQLConnection(String hostName, String dbName, String userName, String password) throws SQLException, ClassNotFoundException {
        Class.forName("");

        String connectionURL ="jdbc:mysql://" + hostName + ":3306/" + dbName;

        Properties info;
        Connection conn = DriverManager.getConnection(connectionURL, userName, password);
        return conn;
    }
}
