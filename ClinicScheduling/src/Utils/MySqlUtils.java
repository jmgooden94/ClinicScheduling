package Utils;

import Models.Appointment.Appointment;
<<<<<<< HEAD
import Models.Day;
=======
>>>>>>> nic-dev
import Models.Provider.Availability;
import Models.Provider.Provider;
import Models.Provider.Recurrence;

import Models.TimeOfDay;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.List;

import org.json.simple.*;

import javax.swing.*;
import javax.xml.transform.Result;

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
    public static void addProvider(Provider provider) throws SQLException{
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
        for (Availability availability : availabilityList){

            // Insert availability and get key
            ps = connection.prepareStatement(sql2, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setTime(1, availability.getStart().toSqlTime());
            ps.setTime(2, availability.getEnd().toSqlTime());

            // Serialize days from availability
            JSONObject jsObj = new JSONObject();
            JSONArray days = new JSONArray();
            for (Day d : availability.getDays()){
                days.add(d.toValueString());
            }
            jsObj.put("days", days);

            ps.setString(3, days.toJSONString());
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
            ps = connection.prepareStatement(sql3, PreparedStatement.RETURN_GENERATED_KEYS);
            jsonString = JSONValue.toJSONString(r);
            ps.setString(1, jsonString);
            rs = ps.getGeneratedKeys();
            ps.setInt(2, availability_id);
            rows = ps.executeUpdate();
            if (rows == 0){
                throw new SQLException("Failed inserting recurrence.");
            }
            connection.commit();
        }
    }

    /**
     * Gets the list of providers and their availabilities from the database, mapped to their id from the database
     * @return the map of providers
     * @throws SQLException
     * @throws ParseException if a JSON field from the database is unable to be parsed
     */
    public static HashMap<Integer, Provider> getProviders() throws SQLException{
        HashMap<Integer, Provider> providersList = new HashMap<>();

        Statement statement = connection.createStatement();
        ResultSet providers = statement.executeQuery("SELECT * FROM clinic.provider");
        HashMap<Integer, List<Availability>> availabilityHashMap = getAvailabilityMap();

        // If there are no providers in the database, return an empty list
        if (!providers.isBeforeFirst()){
            return providersList;
        }

        // For each Result in the ResultSet providers, get their availability from the map, then build the provider
        // object and put it in the map
        while (providers.next()){
            int id = providers.getInt(1);
            List<Availability> availabilityList = availabilityHashMap.get(id);
            String fn = providers.getString(2);
            String ln = providers.getString(3);
            String ptString = providers.getString(4);
            ProviderType pt = ProviderType.fromName(ptString);
            Provider p = new Provider(pt, fn, ln, availabilityList);
            providersList.put(id, p);
        }

        return providersList;
    }

    /**
     * Builds the availability hashmap used to construct providers' availabilities
     * @return the constructed hashmap
     * @throws SQLException
     * @throws ParseException if a JSON field from the database is unable to be parsed
     */
    private static HashMap<Integer, List<Availability>> getAvailabilityMap() throws SQLException {
        Statement statement = connection.createStatement();
        JSONParser parser = new JSONParser();

        ResultSet availabilities = statement.executeQuery("SELECT clinic.recurrence.stringify, " +
                "clinic.availability.start_time, clinic.availability.end_time, clinic.availability.day_list_stringify, " +
                "clinic.availability.provider_fk FROM clinic.recurrence INNER JOIN " +
                "clinic.availability ON clinic.recurrence.availability_fk=clinic.availability.id");

        // If there are no availabilities in the db, return null
        if (!availabilities.isBeforeFirst()){
            return null;
        }

        // For each Result in the availabilities ResultSet, construct a Java Availability and map it to its provider_id
        HashMap<Integer, List<Availability>> availabilityHashMap = new HashMap<>();
        String recJSON;
        String daysJSON;
        while(availabilities.next()) {
            recJSON = availabilities.getString(1);
            Recurrence rec;
            try {
                rec = Recurrence.fromJSONString(recJSON);
            } catch (ParseException ex){
                throw new IllegalArgumentException("Failed parsing Recurrence JSON object.", ex);
            } catch (NoSuchMethodException ex){
                throw new RuntimeException("Implementation of Recurrence interface is missing no-op constructor.", ex);
            }
            Time start_time = availabilities.getTime(2);
            TimeOfDay startTime = TimeOfDay.fromSqlTime(start_time);
            Time end_time = availabilities.getTime(3);
            TimeOfDay endTime = TimeOfDay.fromSqlTime(end_time);

            daysJSON = availabilities.getString(4);
            List<String> daysNames = new ArrayList<>();
            JSONArray daysArr;
            try {
                daysArr = (JSONArray) parser.parse(daysJSON);
            } catch (ParseException ex){
                throw new IllegalArgumentException("Failed parsing days array from database.", ex);
            }
            Iterator<String> it = daysArr.iterator();
            while (it.hasNext()) {
                daysNames.add(it.next());
            }
            List<Day> days = new ArrayList<>();
            for (String s : daysNames) {
                days.add(Day.valueOf(s));
            }
            Availability a = new Availability(rec, days, startTime, endTime);
            Integer providerKey = availabilities.getInt(5);
            if (availabilityHashMap.containsKey(providerKey)) {
                availabilityHashMap.get(providerKey).add(a);
            } else {
                List<Availability> aList = new ArrayList<>();
                aList.add(a);
                availabilityHashMap.put(providerKey, aList);
            }
        }
        return availabilityHashMap;
    }
}
