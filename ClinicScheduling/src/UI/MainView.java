// Some code in this class is copied from or based off code by Marty Strep of The University of Washington
package UI;

import Models.Appointment.Appointment;
import Models.Day;
import UI.Dialogs.*;
import UI.Panels.AppointmentView;
import UI.Panels.ProviderView;
import Utils.MySqlUtils;
import Utils.UserRole;

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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private boolean weeklyView;

    public MainView(UserRole role) {
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
            public void actionPerformed(ActionEvent e) {
                new AddProviderDialog();
            }
        });

        if(role == UserRole.ADMIN){
            createAdminControls();
        }

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(null);
        setContentPane(contentPane);
        setVisible(true);
        createAppointmentView();
        weeklyView = false;
    }

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
        new NewApptDialog();
    }

    /**
     * Toggles between provider and monthly views
     */
    private void onToggle(){
        calendarPanel.removeAll();
        if (weeklyView){
            toggleViewButton.setText("Provider View");
            createAppointmentView();
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
    private void createAppointmentView()
    {
        List<Appointment> l = createBullshitAppointments();
        AppointmentView model = new AppointmentView(l);
        calendarPanel.add(model.getDayView(), BorderLayout.CENTER);
    }

    private List<Appointment> createBullshitAppointments()
    {
        List<Appointment> list = new ArrayList<>();
        Appointment a = new Appointment(null, null, "Fuck you",
                new GregorianCalendar(2016, 1, 11, 10, 30),
                new GregorianCalendar(2016, 1, 11, 11, 0));
        a.setTest(1);
        list.add(a);
        Appointment b = new Appointment(null, null, "Double fuck you",
                new GregorianCalendar(2016, 1, 11, 9, 30),
                new GregorianCalendar(2016, 1, 11, 9, 45));
        b.setTest(2);
        list.add(b);
        Appointment c = new Appointment(null, null, "Triple fuck you",
                new GregorianCalendar(2016, 1, 11, 9, 30),
                new GregorianCalendar(2016, 1, 11, 9, 45));
        c.setTest(3);
        list.add(c);
        Appointment d = new Appointment(null, null, "Triple fuck you",
                new GregorianCalendar(2016, 1, 11, 9, 45),
                new GregorianCalendar(2016, 1, 11, 10, 30));
        d.setTest(4);
        list.add(d);
        return list;
    }

    /**
     * Creates the admin controls and adds them to the panel
     */
    private void createAdminControls(){
        JButton addUser = new JButton("Add User");
        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewUser();
            }
        });
        adminControlPanel.add(addUser, BorderLayout.NORTH);

        JButton editUser = new JButton("Change User Password");
        editUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditUser();
            }
        });
        adminControlPanel.add(editUser, BorderLayout.CENTER);

        JButton deleteUser = new JButton("Delete User");
        deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteUser();
            }
        });
        adminControlPanel.add(deleteUser, BorderLayout.SOUTH);
    }

    /**
     * Shows the add user dialog
     */
    private void onNewUser(){
        new AddUserDialog();
    }

    /**
     * Shows the delete user dialog
     */
    private void onDeleteUser(){
        new DeleteUserDialog();
    }

    /**
     * Shows the edit user dialog
     */
    private void onEditUser(){
        new ChangeUserPasswordDialog();
    }

    /**
     * Shows an error dialog with the exception message
     * @param ex the exception to display
     */
    private void showError(Exception ex){
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
    }
}
