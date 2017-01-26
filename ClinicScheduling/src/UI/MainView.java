package UI;

import Models.Appointment.Appointment;
import Models.Provider.Provider;
import Models.Provider.ProviderType;
import Models.TimeOfDay;
import UI.Dialogs.*;

import Utils.MySqlUtils;
import Utils.UserRole;
import UI.Panels.AllProviderView;
import UI.Panels.AppointmentView;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import javafx.util.Pair;


public class MainView extends JFrame {

    private JButton leftArrowButton;
    private JButton rightArrowButton;
    private JButton newApptButton;
    private JLabel dateLabel;
    private JButton newProvButton;
    private JButton toggleViewButton;
    private JPanel contentPane;
    private JLabel providerLabel;
    private JPanel providerPanel;
    private JPanel calendarPanel;
    private JPanel leftPanel;
    private JPanel adminControlPanel;
    private boolean apptView;
    private HashMap<Integer, Provider> providerMap;
    private SimpleDateFormat dateFormater = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    // This is the date used for the label and should be set to exactly midnight
    // on the current date
    private GregorianCalendar displayedDate = new GregorianCalendar();

    public MainView(UserRole role)
    {
        this.setTitle("Clinic Scheduler");

        providerPanel.setPreferredSize(new Dimension(180, 10));

        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        toggleViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onToggle();
            }
        });

        newApptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNewAppt();
            }
        });

        newProvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){onAddProvider(); }
        });

        // TODO: JMG comment this
        if(role == UserRole.ADMIN){
            createAdminControls();
        }

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(null);
        setContentPane(contentPane);
        setVisible(true);

        displayedDate.set(Calendar.HOUR_OF_DAY, 0);
        displayedDate.set(Calendar.MINUTE, 0);
        displayedDate.set(Calendar.SECOND, 0);
        try {
            providerMap = MySqlUtils.getProviders();
        }
        catch (SQLException ex){
            showError(ex);
        }

        updateProviderPanel();

        createAppointmentView(displayedDate);
        apptView = false;
    }

    //TODO: consider removing this
    public HashMap<Integer, Provider> getProviderMap(){
        return providerMap;
    }

    private void createProviderView(GregorianCalendar date)
    {
        List<Provider> providers = new ArrayList<>();
        try
        {
            // PSEUDO-HERE
            // Map optional, comments in MySQLUtils
            providers = MySqlUtils.getProvidersForDay(date, providerMap);
        }
        catch (SQLException ex)
        {
            showError(ex);
        }
        List<Provider> l = this.createBullshitProviders();
        AllProviderView ap = new AllProviderView(l);
        calendarPanel.add(ap.getView(), BorderLayout.CENTER);
        dateLabel.setText(dateFormater.format(date.getTime()));
    }

    private List<Provider> createBullshitProviders()
    {
        List<Provider> l = new ArrayList<>();

        Provider a = new Provider(ProviderType.LAB, "John", "Doe",null);
        a.start = new TimeOfDay(9, 30);
        a.end = new TimeOfDay(12, 30);
        l.add(a);

        Provider b = new Provider(ProviderType.LAB, "Jane", "Doe", null);
        b.start = new TimeOfDay(12, 45);
        b.end = new TimeOfDay(15, 0);
        l.add(b);

        Provider c = new Provider(ProviderType.LAB, "Jim", "Bob", null);
        c.start = new TimeOfDay(11, 30);
        c.end = new TimeOfDay(13, 45);
        l.add(c);

        return l;
    }

    private void onCancel() {
        dispose();
        try{
            MySqlUtils.closeConnection();
        }
        catch (SQLException ex){
            showError(ex);
        }
        System.exit(0);
    }

    private void onNewAppt()
    {
        NewApptDialog d = new NewApptDialog(providerMap);
        if(d.showDialog() == JOptionPane.OK_OPTION)
        {
            // TODO: replace with tuple??
            Pair<Appointment, Integer> apptData = d.getResult();
            try {
                MySqlUtils.addAppointment(apptData.getKey(), apptData.getValue());
                if (apptView && (apptData.getKey().getApptStart().after(displayedDate) || apptData.getKey().getApptEnd().before(displayedDate))){
                    updateApptView();
                }
            }
            catch (SQLException ex){
                showError(ex);
            }
            if (apptView){
                updateApptView();
            }
        }
    }

    /**
     * Toggles between provider and monthly views
     */
    private void onToggle(){
        calendarPanel.removeAll();
        if (apptView){
            toggleViewButton.setText("Provider View");
            createAppointmentView(displayedDate);
        }
        else {
            toggleViewButton.setText("Appointment View");
            createProviderView(new GregorianCalendar());
        }
        calendarPanel.updateUI();
        apptView = !apptView;
    }

    /**
     * Creates a new monthly view and adds it to the calendar panel
     */
    private void createAppointmentView(GregorianCalendar date)
    {
        List<Appointment> appts = new ArrayList<>();
        try
        {
            GregorianCalendar g = new GregorianCalendar(date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
            appts = MySqlUtils.getAppointments(date, g, providerMap);
        }
        catch (SQLException ex)
        {
            showError(ex);
        }
        AppointmentView model = new AppointmentView(appts);
        calendarPanel.add(model.getView(), BorderLayout.CENTER);
        dateLabel.setText(dateFormater.format(date.getTime()));
    }

    // TODO: remove, only for testing
    private List<Appointment> createBullshitAppointments()
    {
        List<Appointment> list = new ArrayList<>();
        Appointment a = new Appointment(null, null, "Fuck you",
                new GregorianCalendar(2016, 1, 11, 10, 30),
                new GregorianCalendar(2016, 1, 11, 11, 0),
                null);
        a.setTest(1);
        list.add(a);
        Appointment b = new Appointment(null, null, "Double fuck you",
                new GregorianCalendar(2016, 1, 11, 9, 30),
                new GregorianCalendar(2016, 1, 11, 9, 45),
                null);
        b.setTest(2);
        list.add(b);
        Appointment c = new Appointment(null, null, "Triple fuck you",
                new GregorianCalendar(2016, 1, 11, 9, 30),
                new GregorianCalendar(2016, 1, 11, 9, 45),
                null);
        c.setTest(3);
        list.add(c);
        Appointment d = new Appointment(null, null, "Triple fuck you",
                new GregorianCalendar(2016, 1, 11, 9, 45),
                new GregorianCalendar(2016, 1, 11, 10, 30),
                null);
        d.setTest(4);
        list.add(d);
        Appointment e = new Appointment(null, null, "Triple fuck you",
                new GregorianCalendar(2016, 1, 11, 10, 30),
                new GregorianCalendar(2016, 1, 11, 11, 00),
                null);
        e.setTest(5);
        list.add(e);
        return list;
    }

    /**
     * Creates the admin controls and adds them to the panel
     */
    private void createAdminControls()
    {
        GridLayout g = new GridLayout(0, 1, 5, 5);
        adminControlPanel.setLayout(g);

        JButton addUser = new JButton("Add User");
        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewUser();
            }
        });
        adminControlPanel.add(addUser);

        JButton editUser = new JButton("Change User Password");
        editUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onChangePassword();
            }
        });
        adminControlPanel.add(editUser);

        JButton deleteUser = new JButton("Delete User");
        deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteUser();
            }
        });
        adminControlPanel.add(deleteUser);
    }

    /**
     * Shows the add user dialog
     */
    private void onNewUser()
    {
        new AddUserDialog().showDialog();
    }

    private void onAddProvider()
    {
        AddProviderDialog apd = new AddProviderDialog();
        if (apd.showDialog() == JOptionPane.OK_OPTION)
        {
            Provider p = apd.getResult();
            providerMap.put(p.getId(), p);
            updateProviderPanel();
            if (!apptView){
                updateProviderView();
            }
        }
    }

    /**
     * Shows the delete user dialog
     */
    private void onDeleteUser(){
        new DeleteUserDialog().showDialog();
    }

    /**
     * Shows the edit user dialog
     */
    private void onChangePassword(){
        new ChangeUserPasswordDialog().showDialog();
    }

    /**
     * Shows an error dialog with the exception message
     * @param ex the exception to display
     */
    private void showError(Exception ex){
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the provider panel to show the list of providers
     */
    private void updateProviderPanel(){
        providerPanel.removeAll();
        for(Provider p : providerMap.values()){
            JButton pButton = new JButton();
            pButton.setText(p.getName() + ", " + p.getProviderType().getAbbreviation() + "\n");
            providerPanel.add(pButton);
        }
    }

    /**
     * Updates the appointment view
     */
    private void updateApptView(){
        calendarPanel.removeAll();
        createAppointmentView(displayedDate);
        calendarPanel.updateUI();
    }

    private void updateProviderView(){
        calendarPanel.removeAll();
        createProviderView(displayedDate);
        calendarPanel.updateUI();
    }

    /**
     * Decreases the displayed date
     */
    private void onLeftArrow(){
        displayedDate.add(Calendar.DAY_OF_MONTH, -1);
        if(apptView){
            updateApptView();
        }
        else{
            updateProviderView();
        }
    }

    /**
     * Decreases the displayed date
     */
    private void onRightArrow(){
        displayedDate.add(Calendar.DAY_OF_MONTH, 1);
        if(apptView){
            updateApptView();
        }
        else{
            updateProviderView();
        }
    }
}
