package UI;

import Models.Appointment.Appointment;
import Models.Provider.Provider;
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
    private JButton refreshButton;
    private JButton leftMonthButton;
    private JButton rightMonthButton;
    private JButton jumpDateButton;
    private boolean apptView;
    private HashMap<Integer, Provider> providerMap;
    private SimpleDateFormat dateFormater = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    // This is the date used for the label and should be set to exactly midnight
    // on the current date
    private GregorianCalendar displayedDate = new GregorianCalendar();

    public MainView(UserRole role)
    {
        this.setTitle("Clinic Scheduler");

        providerPanel.setPreferredSize(new Dimension(190, 10));
        this.setResizable(false);

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

        leftArrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLeftArrow();
            }
        });

        rightArrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRightArrow();
            }
        });

        leftMonthButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            { onLeftMonth();}
        });

        rightMonthButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            { onRightMonth();}
        });

        jumpDateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            { onJumpDate();}
        });

        refreshButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onRefresh();
            }
        });

        // If the User has the ADMIN role in the database, set up the admin control panel
        if(role == UserRole.ADMIN){
            createAdminControls();
        }

        setSize(1044, 700);
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
        apptView = true;
    }

    private void createProviderView(GregorianCalendar date)
    {
        List<Provider> providers = new ArrayList<>();
        try
        {
            providers = MySqlUtils.getProvidersForDay(date, providerMap);
        }
        catch (SQLException ex)
        {
            showError(ex);
        }
        AllProviderView ap = new AllProviderView(providers);
        calendarPanel.add(ap.getView(), BorderLayout.CENTER);
        dateLabel.setText(dateFormater.format(date.getTime()));
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
            Pair<Appointment, Integer> apptData = d.getResult();
            try {
                MySqlUtils.addAppointment(apptData.getKey(), apptData.getValue());
                GregorianCalendar endOfDay = new GregorianCalendar();
                endOfDay.setTime(displayedDate.getTime());
                endOfDay.add(Calendar.DAY_OF_MONTH, 1);
                if (apptView && (apptData.getKey().getApptStart().after(displayedDate) && apptData.getKey().getApptEnd().before(endOfDay))){
                    updateApptView();
                }
            }
            catch (SQLException ex){
                showError(ex);
            }
        }
    }

    /**
     * Toggles between provider and monthly views
     */
    private void onToggle(){
        calendarPanel.removeAll();
        if (apptView){
            toggleViewButton.setText("Appointment View");
            createProviderView(displayedDate);
        }
        else {
            toggleViewButton.setText("Provider View");
            createAppointmentView(displayedDate);
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
        AppointmentView model = new AppointmentView(appts, this);
        calendarPanel.add(model.getView(), BorderLayout.CENTER);
        dateLabel.setText(dateFormater.format(date.getTime()));
    }

    /**
     * Creates the admin controls and adds them to the panel
     */
    private void createAdminControls()
    {
        GridLayout g = new GridLayout(0, 1, 5, 5);
        adminControlPanel.setLayout(g);

        JButton getStats = new JButton("Get Statistics");
        getStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGetStats();
            }
        });
        adminControlPanel.add(getStats);

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
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the provider panel to show the list of providers
     */
    private void updateProviderPanel(){
        providerPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        for(Provider p : providerMap.values()){
            JButton pButton = new JButton();
            pButton.setText(p.getName() + ", " + p.getProviderType().getAbbreviation());
            pButton.setActionCommand(Integer.toString(p.getId()));
            pButton.addActionListener(providerButtonListener);
            panel.add(pButton);
        }

        // Gotta do this so the buttons don't expand
        // to take up the whole height of the scroll pane
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
        container.add(panel);
        JScrollPane sp = new JScrollPane(container,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        providerPanel.add(sp);
        providerPanel.updateUI();
    }

    /**
     * Updates the appointment view
     */
    public void updateApptView(){
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

    /**
     * Decreases the displayed date by one month
     */
    private void onLeftMonth(){
        displayedDate.add(Calendar.MONTH, -1);
        if(apptView){
            updateApptView();
        }
        else{
            updateProviderView();
        }
    }

    /**
     * Decreases the displayed date by one month
     */
    private void onRightMonth(){
        displayedDate.add(Calendar.MONTH, 1);
        if(apptView){
            updateApptView();
        }
        else{
            updateProviderView();
        }
    }

    /**
     * Displays a dialog to set a date to jump to, then jumps to that date
     */
    private void onJumpDate()
    {
        JumpDateDialog d = new JumpDateDialog();
        if (d.showDialog() == JOptionPane.OK_OPTION)
        {
            displayedDate = d.getSelected();
            if (apptView)
            {
                updateApptView();
            }
            else
            {
                updateProviderView();
            }
        }
    }

    /**
     * Event handler for get stats admin button
     */
    private void onGetStats()
    {
        new GetStatsDialog();
    }

    /**
     * Action listener for when the provider buttons are clicked.
     */
    private ActionListener providerButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            int id = Integer.parseInt(command);
            Provider p = providerMap.get(id);
            // THis is a modal dialog, so execution pauses
            ProviderViewDialog d = new ProviderViewDialog(p);
            if(d.showDialog() == JOptionPane.OK_OPTION)
            {
                if(d.getProviderDeleted())
                {
                    providerMap.remove(p.getId());
                    updateProviderPanel();
                }
            }
            if (!apptView)
            {
                updateProviderView();
            }
        }
    };

    private void onRefresh()
    {
        try
        {
            MySqlUtils.closeConnection();
        }
        catch (SQLException ex)
        {
            showError(ex);
        }
        LoginDialog d = new LoginDialog(false);
        if (d.showDialog() == JOptionPane.OK_OPTION)
        {
            try
            {
                providerMap = MySqlUtils.getProviders();
            }
            catch (SQLException ex)
            {
                showError(ex);
            }
            if(apptView)
            {
                updateApptView();
            }
            else
            {
                updateProviderView();
            }
            updateProviderPanel();
        }
        else
        {
            JOptionPane.showMessageDialog(contentPane, "Database connection has been closed, but not reopened. " +
                            "Close and re-open the application to continue use.", "Database Closed",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
