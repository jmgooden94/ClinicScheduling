package UI;

import Models.Appointment.Appointment;
import Models.Patient.Patient;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NewApptDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel formPanel;
    private JTextField lastNameBox;
    private JTextField firstNameBox;
    private JFormattedTextField phoneBox;
    private JTextField streetBox;
    private JTextField cityBox;
    private JFormattedTextField zipBox;
    private JSpinner startMinuteBox;
    private JSpinner startPMBox;
    private JSpinner startHourBox;
    private JPanel datePickerPanel;
    private JSpinner endHourBox;
    private JTextPane reasonBox;
    private JSpinner stateSpinner;
    private JSpinner endMinuteBox;
    private JSpinner endPMBox;
    private JSpinner providerSpinner;
    private JDatePickerImpl jDatePicker;

    public NewApptDialog() {
        createComponents();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(600,600);
        setLocationRelativeTo(null);
        setVisible(true);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        Patient newPatient = new Patient(firstNameBox.getText(), lastNameBox.getText(), phoneBox.getText(), streetBox.getText(), cityBox.getText(), Models.Patient.State.valueOf((String)stateSpinner.getValue()), Integer.parseInt(zipBox.getText()));
        int year = jDatePicker.getModel().getYear();
        int month = jDatePicker.getModel().getMonth();
        int day = jDatePicker.getModel().getDay();
        int startHour = (int) startHourBox.getValue();
        int endHour = (int) endHourBox.getValue();
        if ((boolean) startPMBox.getValue()){
            startHour += 12;
        }
        if ((boolean) endPMBox.getValue()){
            endHour += 12;
        }
        GregorianCalendar start = new GregorianCalendar(year, month, day, startHour, (int) startMinuteBox.getValue());
        GregorianCalendar end = new GregorianCalendar(year, month, day, endHour, (int) endMinuteBox.getValue());
        //TODO: get provider from list of providers and replace null with provider
        Appointment newAppt = new Appointment(newPatient, null, reasonBox.getText(), start, end);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createComponents(){
        createDatePickerPanel();
    }

    /**
     * Creates a new JDatePicker and adds it to the datePickerPanel
     */
    private void createDatePickerPanel(){
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        jDatePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {

            private String datePattern = "MMMM dd, yyyy";
            private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

            @Override
            public Object stringToValue(String text) throws ParseException {
                return dateFormatter.parseObject(text);
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value != null) {
                    Calendar cal = (Calendar) value;
                    return dateFormatter.format(cal.getTime());
                }

                return "";
            }
        });
        datePickerPanel.add(jDatePicker);
    }
}
