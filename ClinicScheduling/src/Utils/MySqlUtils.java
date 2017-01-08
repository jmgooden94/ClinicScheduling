package Utils;

import java.sql.*;

import javax.swing.*;

public class MySqlUtils {

    private static final String URL = "jdbc:mysql://localhost/clinic";

    public static Connection openConnection(String username, String password){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection c = DriverManager.getConnection(URL, username, password);
            c.setAutoCommit(false);
            return c;
        }
        catch (ClassNotFoundException e){
            JOptionPane.showMessageDialog(new JFrame(),"Unable to load MySQL driver. Unable to connect to " +
                    "database. Contact developer.", "MySQL Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return null;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(new JFrame(),"Error connecting to database. Verify that MySQL is " +
                    "running and/or check credentials", "MySQL Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Gets the logged in user's role
     * @param c the SQL database connection
     * @param un the logged in user's username
     * @return the user's role
     */
    public UserRole getRole(Connection c, String un) throws SQLException{
        String q = "select role from user where username = ?";
        PreparedStatement query = c.prepareStatement(q);
        query.setString(1, un);
        ResultSet rs = query.executeQuery();
        String s = "";
        UserRole role = null;
        if (rs.first()){
            s = rs.getString(1);
            role = UserRole.valueOf(s.toUpperCase());
            System.out.println(s);
        }
        if (role == null){
            JOptionPane.showMessageDialog(new JFrame(),"User's role not found. Assuming user is not admin." +
                    "Verify that user has been added to database correctly.",
                    "Role Warning", JOptionPane.WARNING_MESSAGE);
            role = UserRole.USER;
        }
        return role;
    }
}
