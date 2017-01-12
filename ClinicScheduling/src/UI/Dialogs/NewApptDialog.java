package UI.Dialogs;

import Models.Appointment.Appointment;
import Models.Patient.*;
import Models.State;
import Models.TimeOfDay;
import Utils.MySqlUtils;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.text.*;
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
    private JSpinner apptTypeSpinner;
    private JDatePickerImpl jDatePicker;

    /**
     * Constructor for NewApptDialog
     */
    public NewApptDialog() {
        createComponents();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(600, 600);
        setLocationRelativeTo(null);

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

        setVisible(true);
    }

    /**
     * Event handler for OK; records data in form and stores
     */
    private void onOK() {

        int startHour = (int) startHourBox.getValue();
        boolean startAM = startPMBox.getValue().toString().equals("AM");
        if(startAM && startHour == 12){
            startHour = 0;
        }
        else if (!startAM) startHour += 12;
        TimeOfDay startTime = new TimeOfDay(startHour, (int) startMinuteBox.getValue());
        int endHour = (int) endHourBox.getValue();
        boolean endAM = endPMBox.getValue().toString().equals("AM");
        if(endAM && endHour == 12){
            endHour = 0;
        }
        else if(!endAM) endHour += 12;
        TimeOfDay endTime = new TimeOfDay(endHour, (int) endMinuteBox.getValue());

        if (validateForm(startTime, endTime)){
            Address patientAddress = new Address(streetBox.getText(), cityBox.getText(),
                    State.fromName((String) stateSpinner.getValue()), zipBox.getValue().toString());
            Patient newPatient = new Patient(firstNameBox.getText(), lastNameBox.getText(),
                    phoneBox.getValue().toString(), patientAddress);
            int year = jDatePicker.getModel().getYear();
            int month = jDatePicker.getModel().getMonth();
            int day = jDatePicker.getModel().getDay();

            GregorianCalendar start = new GregorianCalendar(year, month, day, startHour, (int) startMinuteBox.getValue());
            GregorianCalendar end = new GregorianCalendar(year, month, day, endHour, (int) endMinuteBox.getValue());
            //TODO: get provider from list of providers and replace null with provider
            Appointment newAppt = new Appointment(newPatient, null, reasonBox.getText(), start, end);
            MySqlUtils.addAppointment(newAppt);
            dispose();
        }
    }

    /**
     * Event handler for cancel button
     */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * Setup formatters and other custom component settings
     */
    private void createComponents() {
        // Creates the JDatePicker panel
        createDatePickerPanel();

        // Sets document filters to limit length of inputs
        AbstractDocument fnDoc = (AbstractDocument) firstNameBox.getDocument();
        fnDoc.setDocumentFilter(new Utils.DocumentSizeFilter(100));

        AbstractDocument lnDoc = (AbstractDocument) lastNameBox.getDocument();
        lnDoc.setDocumentFilter(new Utils.DocumentSizeFilter(100));

        AbstractDocument stDoc = (AbstractDocument) streetBox.getDocument();
        stDoc.setDocumentFilter(new Utils.DocumentSizeFilter(100));

        AbstractDocument ciDoc = (AbstractDocument) cityBox.getDocument();
        ciDoc.setDocumentFilter(new Utils.DocumentSizeFilter(100));

        AbstractDocument reDoc = (AbstractDocument) reasonBox.getDocument();
        reDoc.setDocumentFilter(new Utils.DocumentSizeFilter(500));

        // Populates the state spinner
        SpinnerListModel stateList = new SpinnerListModel(State.getNames());
        stateSpinner.setModel(stateList);

        // Sets the limits on the time spinners
        SpinnerNumberModel startHours = new SpinnerNumberModel(1, 1, 12, 1);
        startHourBox.setModel(startHours);
        SpinnerNumberModel endHours = new SpinnerNumberModel(1, 1, 12, 1);
        endHourBox.setModel(endHours);

        SpinnerNumberModel startMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        startMinuteBox.setModel(startMinutes);
        startMinuteBox.setEditor(new JSpinner.NumberEditor(startMinuteBox, "00"));
        SpinnerNumberModel endMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        endMinuteBox.setModel(endMinutes);
        endMinuteBox.setEditor(new JSpinner.NumberEditor(endMinuteBox, "00"));

        String[] pmOps = {"AM", "PM"};
        SpinnerListModel startPm = new SpinnerListModel(Arrays.asList(pmOps));
        SpinnerListModel endPm = new SpinnerListModel(Arrays.asList(pmOps));
        startPMBox.setModel(startPm);
        endPMBox.setModel(endPm);

        // Populates the provider types spinner
        SpinnerListModel provTypes = new SpinnerListModel(Models.Provider.ProviderType.getNames());
        apptTypeSpinner.setModel(provTypes);
    }

    /**
     * Creates a new JDatePicker and adds it to the datePickerPanel
     */
    private void createDatePickerPanel() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        jDatePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy");

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

    /**
     * Creates custom UI fields
     */
    private void createUIComponents() {
        try {
            MaskFormatter zipFormat = new MaskFormatter("#####");
            zipBox = new JFormattedTextField(zipFormat);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(contentPane, "Invalid ZIP");
        }

        try {
            MaskFormatter phoneFormat = new MaskFormatter("(###) ###-####");
            phoneBox = new JFormattedTextField(phoneFormat);
        } catch (ParseException e){
            JOptionPane.showMessageDialog(contentPane, "Invalid phone number.");
        }
    }

    /**
     * Validates the new appointment dialog form
     * @param start the start time of the appt
     * @param end the end time of the appt
     * @return true if input is valid; else false
     */
    private boolean validateForm(TimeOfDay start, TimeOfDay end){
        if(firstNameBox.getText() == "" || lastNameBox.getText() == "" || streetBox.getText() == "" ||
                cityBox.getText() == "" || zipBox.getValue() == null || phoneBox.getValue() == null
                ){
                JOptionPane.showMessageDialog(contentPane, "Missing or incorrect form information. " +
                        "Please verify all fields are filled completely.", "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(end.beforeOrEqual(start)){
            JOptionPane.showMessageDialog(contentPane, "End time is before or equal to start time.",
                    "Invalid Times", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
