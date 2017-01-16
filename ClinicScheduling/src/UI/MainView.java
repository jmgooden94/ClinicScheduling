package UI;

import Models.Day;
import Models.Provider.Provider;
import UI.Dialogs.*;
import UI.Panels.ProviderView;
import Utils.MySqlUtils;
import Utils.UserRole;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

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
    private JScrollPane providerScrollPane;
    private boolean weeklyView;
    private HashMap<Integer, Provider> providerMap;

    public MainView(UserRole role) {
        try {
            providerMap = MySqlUtils.getProviders();
        }
        catch (SQLException ex){
            showError(ex);
        }
        providerPanel.setPreferredSize(new Dimension(180, 10));
        providerScrollPane.setViewportView(providerPanel);
        updateProviderPanel();

        this.setTitle("Clinic Scheduler");

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

        if(role == UserRole.ADMIN){
            createAdminControls();
        }

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(null);
        setContentPane(contentPane);
        setVisible(true);
        createMonthView();
        weeklyView = false;
    }

    public HashMap<Integer, Provider> getProviderMap(){
        return providerMap;
    }

    // Some code in this method is copied from or based off code by Marty Strep of The University of Washington
    private void createProviderView(GregorianCalendar date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy");

        date.set(Calendar.DAY_OF_WEEK, 2);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTime(date.getTime());
        endDate.set(Calendar.DAY_OF_WEEK, 6);
        dateLabel.setText(dateFormatter.format(date.getTime()) + " - " + dateFormatter.format(endDate.getTime()));

        AbstractTableModel model = new ProviderView(null, date);
        JTable scheduleTable = new JTable(model);

        // set up the table column headings
        JTableHeader header = scheduleTable.getTableHeader();
        header.setReorderingAllowed(false);
        TableColumnModel columnModel = scheduleTable.getColumnModel();
        columnModel.getColumn(0).setHeaderValue(null);
        for (int c = 1; c < model.getColumnCount(); c++) {
            TableColumn column = columnModel.getColumn(c);
            Day day = Day.toDay(c);
            column.setHeaderValue(day == null ? "" : String.valueOf(day.toString()));
        }
        ListSelectionModel selectionModel = scheduleTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarPanel.add(header, BorderLayout.NORTH);
        calendarPanel.add(scheduleTable, BorderLayout.CENTER);
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

    private void onNewAppt() {
        if(new NewApptDialog().showDialog() == JOptionPane.OK_OPTION){
            //TODO: add the appointment
        }
    }

    /**
     * Toggles between provider and monthly views
     */
    private void onToggle(){
        calendarPanel.removeAll();
        if (weeklyView){
            toggleViewButton.setText("Provider View");
            createMonthView();
        }
        else {
            toggleViewButton.setText("Appointment View");
            createProviderView(new GregorianCalendar());
        }
        calendarPanel.updateUI();
        weeklyView = !weeklyView;
    }

    /**
     * Creates a new monthly view and adds it to the calendar panel
     */
    private void createMonthView(){

    }

    /**
     * Creates a new AddProviderDialog and updates the provider map
     */
    private void onAddProvider(){
        if (new AddProviderDialog().showDialog() == JOptionPane.OK_OPTION){
            try {
                providerMap = MySqlUtils.getProviders();
                updateProviderPanel();
            }
            catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    /**
     * Creates the admin controls and adds them to the panel
     */
    private void createAdminControls(){
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
                onEditUser();
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
    private void onNewUser(){
        new AddUserDialog().showDialog();
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
    private void onEditUser(){
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
        // TODO: figure out how to make a scroll bar appear if the buttons don't fit in the panel
    }
}
