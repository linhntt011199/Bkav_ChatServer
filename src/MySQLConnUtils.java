import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnUtils {
    public static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
        String hostName = "127.0.0.1";

        String dbName = "ChatDB";
        String userName = "root";
        String password = "qazxswedc";

        return getMySQLConnection(hostName, dbName, userName, password);
    }

    public static Connection getMySQLConnection(String hostName, String dbName, String userName, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String connectionURL ="jdbc:mysql://" + hostName + ":3306/" + dbName;

        Properties info;
        Connection conn = DriverManager.getConnection(connectionURL, userName, password);
        return conn;
    }
}
