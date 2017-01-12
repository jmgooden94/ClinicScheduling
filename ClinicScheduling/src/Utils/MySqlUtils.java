package Utils;

import Models.Appointment.Appointment;
import Models.Day;
import Models.Provider.Availability;
import Models.Provider.Provider;
import Models.Provider.Recurrence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
public class MySqlUtils {

    private static final String URL = "jdbc:mysql://localhost/clinic";
    private static Connection connection;
    private static String loggedInUser;

    /**
     * Attempts to open a MySQL connection using the specified username and password and the constant string URL
     * @param username the username to log in with
     * @param password the password to log in with
     * @return boolean indicating if the connection was successful
     */
    public static boolean openConnection(String username, String password){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, username, password);
            connection.setAutoCommit(false);
            loggedInUser = username;
            return true;
        }
        catch (ClassNotFoundException e){
            JOptionPane.showMessageDialog(new JFrame(),"Unable to load MySQL driver. Unable to connect to " +
                    "database. Contact developer.", "MySQL Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return false;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(new JFrame(),"Error connecting to database. Verify that MySQL is " +
                    "running and/or check credentials", "MySQL Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Gets the logged in user's role
     * @param un the logged in user's username
     * @return the user's role
     * @throws SQLException
     */
    public static UserRole getRole(String un) throws SQLException{
        String q = "select role from user where username = ?";
        PreparedStatement query = connection.prepareStatement(q);
        query.setString(1, un);
        ResultSet rs = query.executeQuery();
        String s = "";
        UserRole role = null;
        if (rs.first()){
            s = rs.getString(1);
            role = UserRole.valueOf(s.toUpperCase());
        }
        if (role == null){
            JOptionPane.showMessageDialog(new JFrame(),"User's role not found. Assuming user is not admin." +
                    "Verify that user has been added to database correctly.",
                    "Role Not Found", JOptionPane.WARNING_MESSAGE);
            role = UserRole.USER;
        }
        return role;
    }

    /**
     * Adds the user to the database with the given fields
     * @param username the user's username
     * @param pw the user's password
     * @param role the user's role
     * @throws SQLException
     */
    public static void addUser(String username, String pw, UserRole role) throws SQLException{
        String cs = "CREATE USER ? IDENTIFIED BY ?";
        String is = "INSERT INTO clinic.user(username, role) values(?, ?)";

        PreparedStatement userPrivileges = connection.prepareStatement(cs);
        PreparedStatement insertUser = connection.prepareStatement(is);

        insertUser.setString(1, username);
        insertUser.setString(2, role.toString());

        userPrivileges.setString(1, username);
        userPrivileges.setString(2, pw);

        insertUser.execute();
        userPrivileges.execute();

        if (role == UserRole.ADMIN){
            String ga = "GRANT ALL ON clinic.* TO ? WITH GRANT OPTION";
            String gc = "GRANT CREATE USER ON *.* TO ? WITH GRANT OPTION";

            PreparedStatement grantAll = connection.prepareStatement(ga);
            PreparedStatement grantCreate = connection.prepareStatement(gc);

            grantAll.setString(1, username);
            grantCreate.setString(1, username);

            grantAll.execute();
            grantCreate.execute();
        }
        else{
            String g1 = "GRANT INSERT, SELECT, UPDATE, DELETE ON clinic.address TO ?";
            String g2 = "GRANT INSERT, SELECT, UPDATE, DELETE ON clinic.appointment TO ?";
            String g3 = "GRANT INSERT, SELECT, UPDATE, DELETE ON clinic.availability TO ?";
            String g4 = "GRANT INSERT, SELECT, UPDATE, DELETE ON clinic.patient TO ?";
            String g5 = "GRANT INSERT, SELECT, UPDATE, DELETE ON clinic.provider TO ?";
            String g6 = "GRANT INSERT, SELECT, UPDATE, DELETE ON clinic.recurrence TO ?";
            String g7 = "GRANT SELECT ON clinic.user TO ?";
            String[] gs = {g1, g2, g3, g4, g5, g6, g7};
            PreparedStatement ps;
            for(int i = 0; i < gs.length; i++){
                ps = connection.prepareStatement(gs[i]);
                ps.setString(1, username);
                ps.execute();
            }
        }
        connection.commit();
    }

    /**
     * Deletes the given user from the database
     * @param username the user to delete
     * @throws SQLException
     */
    public static void removeUser(String username) throws SQLException{
        if (username.equals(loggedInUser)){
            throw new IllegalArgumentException("Cannot delete yourself.");
        }
        else if (username.equals("clinic_admin")){
            throw new IllegalArgumentException("Cannot delete default admin account.");
        }
        else {
            String s = "DROP USER ?";
            String s2 = "DELETE FROM clinic.user WHERE username=?";
            PreparedStatement dropUser = connection.prepareStatement(s);
            PreparedStatement deleteUser = connection.prepareStatement(s2);
            dropUser.setString(1, username);
            deleteUser.setString(1, username);
            deleteUser.execute();
            dropUser.execute();
            connection.commit();
        }
    }

    /**
     * Closes the connection to the SQL db
     * @throws SQLException
     */
    public static void closeConnection() throws SQLException{
        connection.close();
    }

    public static List<String> getUsernames() throws SQLException{
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM clinic.user";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            users.add(rs.getString("username"));
        }
        return users;
    }

    /**
     * updates the given user with the given parameters
     * @param username the user's username
     * @param newPassword the user's new password
     * @throws SQLException
     */
    public static void changePassword(String username, String newPassword) throws SQLException{
        String cs = "ALTER USER ? IDENTIFIED BY ?";

        PreparedStatement userPrivileges = connection.prepareStatement(cs);

        userPrivileges.setString(1, username);
        userPrivileges.setString(2, newPassword);

        userPrivileges.execute();
        connection.commit();
    }

    /**
     * Adds the appointment to the database
     * @param appointment the appointment to add
     */
    public static void addAppointment(Appointment appointment){
        //TODO: this
    }

    /**
     * Adds the provider to the database
     * @param provider the provider to add
     * @throws SQLException
     */
    public static void addProvider(Provider provider) throws SQLException {
        PreparedStatement ps;

        // Insert provider and get id to be used as key for availability
        String sql = "INSERT INTO clinic.provider(first_name, last_name, provider_type) values (?, ?, ?)";
        ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, provider.getFirstName());
        ps.setString(2, provider.getLastName());
        ps.setString(3, provider.getProviderType().toString());
        int rows = ps.executeUpdate();
        if (rows == 0){
            throw new SQLException("Failed inserting provider.");
        }
        int provider_id;
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()){
            provider_id = rs.getInt(1);
        }
        else {
            throw new SQLException("Failed getting key after inserting provider.");
        }

        // For each availability, insert it, get its key to be used for recurrence, then insert its recurrence
        List<Availability> availabilityList = provider.getAvailability();
        Recurrence r;
        String sql2 = "INSERT INTO clinic.availability(start_time, end_time, day_list_stringify, provider_fk) values(?, ?, ?, ?)";
        String sql3 = "INSERT INTO clinic.recurrence(stringify, availability_fk) values(?,?)";
        JSONObject jsonObject;
        JSONArray jsonArray;
        for (Availability availability : availabilityList){

            // Insert availability and get key
            ps = connection.prepareStatement(sql2, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setTime(1, availability.getStart().toSqlTime());
            ps.setTime(2, availability.getEnd().toSqlTime());

            // Serialize days from availability
            JSONObject jsObj = new JSONObject();
            JSONArray days = new JSONArray();
            for (Day d : availability.getDays()){
                days.add(days.toString());
            }
            jsObj.put("days", days);

            ps.setString(3, jsObj.toJSONString());
            ps.setInt(4, provider_id);
            rows = ps.executeUpdate();
            if (rows == 0){
                throw new SQLException("Failed inserting availability.");
            }
            rs = ps.getGeneratedKeys();
            int availability_id = -1;
            if(rs.next()){
                availability_id = rs.getInt(1);
            }
            else {
                throw new SQLException("Failed getting key after inserting availability.");
            }
            // Insert recurrence
            r = availability.getRecurrence();
            ps = connection.prepareStatement(sql3);

            ps.setString(1, r.toJSONString());
            ps.setInt(2, availability_id);
            rows = ps.executeUpdate();
            if (rows == 0){
                throw new SQLException("Failed inserting recurrence.");
            }
            connection.commit();
        }
    }

    /**
     * Gets the list of providers and their availabilities from the database
     * @return the list of providers
     */
    public static List<Provider> getProviders() throws SQLException, IOException{
        List<Provider> providersList = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet recurrences = statement.executeQuery("SELECT * FROM clinic.recurrence");
        ResultSet availabilities = statement.executeQuery("SELECT * FROM clinic.availability");
        ResultSet providers = statement.executeQuery("SELECT * FROM clinic.provider");

        while(recurrences.next()){
            // TODO: finish mapping recurrences to availabilities and availabilities to providers
            String json = recurrences.getString(1);
            Recurrence rec = Recurrence.fromJSONString(json);
        }
        return providersList;
    }
}
