package Utils;

import Models.Appointment.Appointment;
import Models.Appointment.ApptStatus;
import Models.Appointment.SpecialType;
import Models.Day;
import Models.Patient.Address;
import Models.Patient.Patient;
import Models.Provider.Availability;
import Models.Provider.Provider;
import Models.Provider.ProviderType;
import Models.State;
import Models.TimeOfDay;

import java.sql.*;
import java.util.*;
import javax.swing.*;

/**
 * Helper class for making queries against the clinic database
 * Note that this class has A LOT of HARDCODED values that are specific to the database schema and queries being used
 * This schema is available in createtables.sql
 */
public class MySqlUtils
{
    private static GlobalConfig config = GlobalConfig.getInstance();

    private static final String URL = config.getUrl();

    private static Connection connection;
    private static String loggedInUser;

    /**
     * Attempts to open a MySQL connection using the specified username and password and the constant string URL
     * @param username the username to log in with
     * @param password the password to log in with
     * @return boolean indicating if the connection was successful
     */
    public static boolean openConnection(String username, String password)
    {
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
    public static UserRole getRole(String un) throws SQLException
    {
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
    public static void addUser(String username, String pw, UserRole role) throws SQLException
    {
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
    public static void removeUser(String username) throws SQLException
    {
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
    public static void closeConnection() throws SQLException
    {
        connection.close();
    }

    public static List<String> getUsernames() throws SQLException
    {
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
     * Adds the provider to the database
     * @param provider the provider to add
     * @throws SQLException
     */
    public static void addProvider(Provider provider) throws SQLException
    {
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
        provider.setId(provider_id);
        addAvailability(provider_id, provider.getAvailability());
        connection.commit();
    }

    /**
     * Adds the list of availabilities to the database
     * @param provider_id the id (from the database) this availability belongs to
     * @param availabilityList the list of availabilities
     * @throws SQLException
     */
    private static void addAvailability(int provider_id, List<Availability> availabilityList) throws SQLException
    {
        PreparedStatement ps;
        String sql = "INSERT INTO clinic.availability(start_time, end_time, provider_fk, monday, tuesday, wednesday, thursday, friday, week) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // For each availability, insert it into the database
        for (Availability availability : availabilityList){
            ps = connection.prepareStatement(sql);
            ps.setTime(1, availability.getStart().toSqlTime());
            ps.setTime(2, availability.getEnd().toSqlTime());
            ps.setInt(3, provider_id);
            // The index of monday in the INSERT sql string (HARDCODED)
            int mondayIndex = 4;
            for (int i = 0; i < GlobalConfig.PROVIDER_WEEK_LENGTH; i++){
                ps.setBoolean(mondayIndex + i, availability.getDays()[i]);
            }
            ps.setInt(9, availability.getWeek());
            int rows = ps.executeUpdate();
            if (rows == 0){
                throw new SQLException("Failed inserting availability.");
            }
        }
    }

    /**
     * Gets the list of providers and their availabilities from the database, mapped to their id from the database
     * @return the map of providers
     * @throws SQLException
     */
    public static HashMap<Integer, Provider> getProviders() throws SQLException
    {
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
            int id = providers.getInt("id");
            List<Availability> availabilityList = availabilityHashMap.get(id);
            String fn = providers.getString("first_name");
            String ln = providers.getString("last_name");
            String ptString = providers.getString("provider_type");
            ProviderType pt = ProviderType.fromName(ptString);
            Provider p = new Provider(pt, fn, ln, availabilityList);
            p.setId(id);
            providersList.put(id, p);
        }

        return providersList;
    }

    /**
     * Builds the availability hashmap used to construct providers' availabilities
     * @return the constructed hashmap; key is the provider's id from the db, value is the list of that provider's
     * availabilities
     * @throws SQLException
     */
    private static HashMap<Integer, List<Availability>> getAvailabilityMap() throws SQLException
    {
        Statement statement = connection.createStatement();

        ResultSet availabilities = statement.executeQuery("SELECT * FROM clinic.availability");

        // If there are no availabilities in the db, return null
        if (!availabilities.isBeforeFirst()){
            return null;
        }

        // For each Result in the availabilities ResultSet, construct a Java Availability and map it to its provider_id
        HashMap<Integer, List<Availability>> availabilityHashMap = new HashMap<>();
        while(availabilities.next()) {
            Availability a = getAvailabilityFromResultSet(availabilities);
            Integer providerKey = availabilities.getInt("provider_fk");
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

    /**
     * Inserts the given address into the database
     * @param a the address to insert
     * @return the id of the address in the address table
     * @throws SQLException
     */
    private static int addAddress(Address a) throws SQLException
    {
        PreparedStatement ps;
        String sql = "INSERT INTO clinic.address(street, city, state, zip) values(?,?,?,?)";
        ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, a.getStreet());
        ps.setString(2, a.getCity());
        ps.setString(3, a.getState().name());
        ps.setString(4, a.getZip());
        int rows = ps.executeUpdate();
        if (rows == 0){
            throw new SQLException("Failed inserting address.");
        }
        int address_id;
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()){
            address_id = rs.getInt(1);
        }
        else {
            throw new SQLException("Failed getting key after inserting address.");
        }
        return address_id;
    }

    /**
     * Checks if the address is already in the database and inserts it if it is not
     * @param a the address to insert
     * @return the id of the address in the address table
     * @throws SQLException
     */
    private static int addAddressIfNotExists(Address a) throws SQLException
    {
        String sql = "SELECT id FROM clinic.address WHERE street=? AND city=? AND state=? AND zip=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, a.getStreet());
        ps.setString(2, a.getCity());
        ps.setString(3, a.getState().name());
        ps.setString(4, a.getZip());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            return rs.getInt(1);
        }
        else {
            return addAddress(a);
        }
    }

    /**
     * Inserts the given patient into the database
     * @param p the patient to insert
     * @return the id of the patient from the patient table
     * @throws SQLException
     */
    private static int addPatient(Patient p) throws SQLException
    {
        int address_fk = addAddressIfNotExists(p.getAddress());
        PreparedStatement ps;
        String sql = "INSERT INTO clinic.patient(first_name, last_name, phone_number, address_fk) values(?,?,?,?)";
        ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, p.getFirstName());
        ps.setString(2, p.getLastName());
        ps.setString(3, p.getPhone());
        ps.setInt(4, address_fk);
        int rows = ps.executeUpdate();
        if (rows == 0){
            throw new SQLException("Failed inserting patient.");
        }
        int address_id;
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()){
            address_id = rs.getInt(1);
        }
        else {
            throw new SQLException("Failed getting key after inserting patient.");
        }
        return address_id;
    }

    /**
     * Checks if the patient is already in the database, updates it if it is, inserts it if it is not
     * @param p the patient to insert
     * @param address_fk the id of the patient's address in the database (in most cases, this will be the value
     *                   returned by addAddressIfNotExists(Address a)
     * @return the id of the patient in the patient table
     * @throws SQLException
     */
    private static int addOrUpdatePatient(Patient p, int address_fk) throws SQLException
    {
        PreparedStatement ps;
        String sql = "SELECT * FROM clinic.patient WHERE first_name=? AND last_name=? AND (phone_number = ? OR address_fk = ?)";
        ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, p.getFirstName());
        ps.setString(2, p.getLastName());
        ps.setString(3, p.getPhone());
        ps.setInt(4, address_fk);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            int pid = rs.getInt("id");
            updatePatient(p, pid, address_fk);
            return pid;
        }
        else {
            return addPatient(p);
        }
    }

    /**
     * Updates an existing patient in the database
     * @param p the patient to update
     * @param pid the patient's id from the database
     * @param address_fk the patient's address foreign key
     * @throws SQLException
     */
    private static void updatePatient(Patient p, int pid, int address_fk) throws SQLException
    {
        String sql = "UPDATE clinic.patient SET clinic.patient.phone_number=?, clinic.patient.address_fk=?" +
                " WHERE clinic.patient.id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, p.getPhone());
        ps.setInt(2, address_fk);
        ps.setInt(3, pid);
        ps.executeUpdate();
    }

    /**
     * Adds the appointment to the database
     * @param appointment the appointment to add
     * @param provider_id the id from the database of the provider serving this appointment
     */
    public static void addAppointment(Appointment appointment, int provider_id) throws SQLException
    {
        int address_id = addAddressIfNotExists(appointment.getPatient().getAddress());
        int patient_id = addOrUpdatePatient(appointment.getPatient(), address_id);
        String sql = "INSERT INTO clinic.appointment(reason, start_time, end_time, provider_fk, patient_fk, appt_type, smoker)" +
                " values(?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        String reason = appointment.getReason();
        ps.setString(1, reason);
        Timestamp apptStart = new Timestamp(appointment.getApptStart().getTimeInMillis());
        ps.setTimestamp(2, apptStart);
        Timestamp apptEnd = new Timestamp(appointment.getApptEnd().getTimeInMillis());
        ps.setTimestamp(3, apptEnd);
        ps.setInt(4, provider_id);
        ps.setInt(5, patient_id);
        SpecialType s = appointment.getSpecialType();
        if (s != null){
            ps.setString(6, appointment.getSpecialType().name());
        }
        else {
            ps.setNull(6, Types.VARCHAR);
        }
        ps.setBoolean(7, appointment.getSmoker());
        ps.execute();
        connection.commit();
    }

    /**
     * Builds a list containing all appointments in the given date range
     * @param start the beginning of the date range
     * @param end the end of the date range
     * @param providerMap the map of providers updated by the getProviders method
     * @return
     */
    public static List<Appointment> getAppointments(GregorianCalendar start, GregorianCalendar end,
                                                    Map<Integer, Provider> providerMap) throws SQLException
    {
        String sql = "select * from clinic.appointment JOIN clinic.patient ON " +
                "clinic.appointment.patient_fk=clinic.patient.id JOIN clinic.address ON " +
                "clinic.patient.address_fk=clinic.address.id WHERE clinic.appointment.status IS NULL" +
                " AND start_time BETWEEN ? AND ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Timestamp begin = new Timestamp(start.getTimeInMillis());
        Timestamp stop = new Timestamp(end.getTimeInMillis());
        ps.setTimestamp(1, begin);
        ps.setTimestamp(2, stop);
        ResultSet rs = ps.executeQuery();
        return constructApptsFromResultSet(rs, providerMap);
    }

    /**
     * Constructs a list of appointments from the resultset in getAppointments; this method should ONLY be called
     * using that result set as the parameter
     * @param rs the result set created by getAppointments
     * @param providerMap the map of providers to their ids, should be passed through by getAppointments
     * @return a list of appointments constructed from that result set
     */
    private static List<Appointment> constructApptsFromResultSet(ResultSet rs, Map<Integer, Provider> providerMap)
            throws SQLException
    {
        List<Appointment> appointments = new ArrayList<>();
        GregorianCalendar sc = new GregorianCalendar();
        GregorianCalendar ec = new GregorianCalendar();
        while(rs.next()){
            Address a = new Address(rs.getString("street"), rs.getString("city"), State.valueOf(rs.getString("state")), rs.getString("zip"));
            Patient p = new Patient(rs.getString("first_name"), rs.getString("last_name"), rs.getString("phone_number"), a);
            Timestamp s = rs.getTimestamp("start_time");
            sc.setTimeInMillis(s.getTime());
            Timestamp e = rs.getTimestamp("end_time");
            ec.setTimeInMillis(e.getTime());
            String typeString = rs.getString("appt_type");
            SpecialType st = null;
            if (typeString != null){
                st = SpecialType.valueOf(typeString);
            }
            Appointment appt = new Appointment(p, providerMap.get(rs.getInt("provider_fk")), rs.getString("reason") , sc, ec, st, rs.getBoolean("smoker"));
            appointments.add(appt);
        }
        return appointments;
    }

    public static List<Provider> getProvidersForDay(GregorianCalendar date, HashMap<Integer, Provider> map) throws SQLException
    {
        String day = Day.values()[date.get(Calendar.DAY_OF_WEEK) - 1].getName().toLowerCase();
        List<Provider> providers = new ArrayList<>();
        // Query DB to get availability objects/provider id's/objects for the provided day
        String sql = "SELECT * FROM clinic.provider JOIN clinic.availability ON " +
                "clinic.provider.id=clinic.availability.provider_fk WHERE ((clinic.availability.week=? OR " +
                "clinic.availability.week=0) AND clinic.availability." + day + "=1)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, date.get(Calendar.WEEK_OF_MONTH));
        // Calendar.DAY_OF_WEEK - 2 because Day.values is 0-indexed and excludes weekends, whereas Calendar.DAY_OF_WEEK
        // is 1-indexed starting with Sunday
        ResultSet rs = ps.executeQuery();
        // If no results, return empty list
        if (!rs.isBeforeFirst()){
            return providers;
        }
        while(rs.next()){
            int id = rs.getInt("id");
            Provider p = map.get(id);
            Availability a = getAvailabilityFromResultSet(rs);
            p.setStart(a.getStart());
            p.setEnd(a.getEnd());
            providers.add(p);
        }
        return providers;
    }

    /**
     * Constructs an availability from a single line of a result set
     * @param rs the result set
     * @return the availability
     */
    private static Availability getAvailabilityFromResultSet(ResultSet rs) throws SQLException
    {
        Time start_time = rs.getTime("start_time");
        TimeOfDay startTime = TimeOfDay.fromSqlTime(start_time);
        Time end_time = rs.getTime("end_time");
        TimeOfDay endTime = TimeOfDay.fromSqlTime(end_time);
        boolean u = rs.getBoolean("sunday");
        boolean m = rs.getBoolean("monday");
        boolean t = rs.getBoolean("tuesday");
        boolean w = rs.getBoolean("wednesday");
        boolean r = rs.getBoolean("thursday");
        boolean f = rs.getBoolean("friday");
        boolean s = rs.getBoolean("saturday");
        boolean[] days = {u, m, t, w, r, f, s};
        int week = rs.getInt("week");

        // Construct availability object from database values
        return new Availability(days, startTime, endTime, week);
    }

    /**
     * Gets the number of patients in the database who are smokers
     * @return the number of patients in the database who are smokers
     * @throws SQLException
     */
    public static int getSmokerCount() throws SQLException
    {
        String sql = "SELECT COUNT(DISTINCT clinic.appointment.patient_fk) FROM clinic.appointment WHERE clinic.appointment.smoker=1";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt(1);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets the number of each special type of appointment
     * @param start the beginning date of the range to get stats for
     * @param end the end date of the range to get stats for
     * @return hashmap with key of special_type and value of the number of occurrences of that type of appt
     * @throws SQLException
     */
    public static HashMap<String, Integer> getSpecialTypeCounts(GregorianCalendar start, GregorianCalendar end) throws
            SQLException
    {
        HashMap<String, Integer> typeCounts = new HashMap<>();
        String sql = "SELECT clinic.appointment.appt_type FROM clinic.appointment WHERE clinic.appointment.appt_type " +
                "IS NOT NULL AND clinic.appointment.appt_type != \"" + SpecialType.PROVIDER_UNAVAILABLE.toString() +
                "\" AND clinic.appointment.start_time BETWEEN ? AND ? AND clinic.appointment.status IS NULL";
        PreparedStatement ps = connection.prepareStatement(sql);
        Timestamp begin = new Timestamp(start.getTimeInMillis());
        Timestamp stop = new Timestamp(end.getTimeInMillis());
        ps.setTimestamp(1, begin);
        ps.setTimestamp(2, stop);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst())
        {
            while (rs.next())
            {
                String t = rs.getString("appt_type");
                if(!typeCounts.containsKey(t))
                {
                    typeCounts.put(t, 1);
                }
                else
                {
                    int c = typeCounts.get(t);
                    typeCounts.replace(t, c+1);
                }
            }
        }
        for (SpecialType st : SpecialType.values())
        {
            if(!typeCounts.containsKey(st.toString()))
            {
                typeCounts.put(st.toString(), 0);
            }
        }
        return typeCounts;
    }

    /**
     * Gets the number of each special type of appointment
     * @param start the beginning date of the range to get stats for
     * @param end the end date of the range to get stats for
     * @return hashmap with key of special_type and value of the number of occurrences of that type of appt
     * @throws SQLException
     */
    public static HashMap<String, Integer> getCancellationCounts(GregorianCalendar start, GregorianCalendar end)
            throws SQLException
    {
        HashMap<String, Integer> cancelCounts = new HashMap<>();
        String sql = "SELECT clinic.appointment.status FROM clinic.appointment WHERE clinic.appointment.status IS NOT " +
                "NULL AND clinic.appointment.appt_type != \"" + SpecialType.PROVIDER_UNAVAILABLE.toString() +
                "\" AND clinic.appointment.start_time BETWEEN ? AND ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        Timestamp begin = new Timestamp(start.getTimeInMillis());
        Timestamp stop = new Timestamp(end.getTimeInMillis());
        ps.setTimestamp(1, begin);
        ps.setTimestamp(2, stop);
        ResultSet rs = ps.executeQuery();
        if (!rs.isBeforeFirst())
        {
            while (rs.next())
            {
                String t = rs.getString("status");
                if(!cancelCounts.containsKey(t))
                {
                    cancelCounts.put(t, 1);
                }
                else
                {
                    int c = cancelCounts.get(t);
                    cancelCounts.replace(t, c+1);
                }
            }
        }
        for (ApptStatus st : ApptStatus.values())
        {
            if(!cancelCounts.containsKey(st.toString()))
            {
                cancelCounts.put(st.toString(), 0);
            }
        }
        return cancelCounts;
    }

    /**
     * Closes and reopens the database connection with the same logged in user
     * @param password the user's password (should be re-entered_
     * @return if it was successful
     * @throws SQLException
     */
    public static boolean refreshConnection(String password) throws SQLException
    {
        connection.close();
        return openConnection(loggedInUser, password);
    }
}