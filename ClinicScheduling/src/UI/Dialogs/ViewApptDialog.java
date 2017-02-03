package UI.Dialogs;

import Models.Appointment.Appointment;
import Models.Appointment.SpecialType;
import Models.Patient.*;
import Models.Provider.Availability;
import Models.Provider.Provider;
import Models.State;
import Models.TimeOfDay;
import Utils.GlobalConfig;
import Utils.MySqlUtils;
import com.sun.javafx.geom.AreaOp;
import javafx.util.Pair;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.MaskFormatter;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.*;
import java.util.*;

public class ViewApptDialog extends JDialog
{
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
    private JSpinner endMinuteBox;
    private JSpinner endPMBox;
    private JComboBox specialTypesCombo;
    private JComboBox providerCombo;
    private JComboBox stateCombo;
    private JCheckBox smokerCheckBox;
    private JDatePickerImpl jDatePicker;
    private int dialogResult = -1;
    private HashMap<Integer, Provider> providerMap;
    /**
     * The results of the appointment dialog; a new appointment, and the id of the provider servicing it
     */
    private Pair<Appointment, Integer>  result;
    /**
     * The index of the default state in the dropdown box
     */
    private static final int DEFAULT_STATE_INDEX = State.KANSAS.ordinal();

    /**
     * Constructor for NewApptDialog
     */
    // TODO: when you click on phone number box and zip, it's always at the beginning JMG
    // TODO: pop dialog when user clicks in date box or do something else ???
    public ViewApptDialog(HashMap<Integer, Provider> providerMap) {
        this.providerMap = providerMap;
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
    }

    public int showDialog(){
        setVisible(true);
        return dialogResult;
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
        else if (!startAM && startHour != 12) startHour += 12;
        TimeOfDay startTime = new TimeOfDay(startHour, (int) startMinuteBox.getValue());
        int endHour = (int) endHourBox.getValue();
        boolean endAM = endPMBox.getValue().toString().equals("AM");
        if(endAM && endHour == 12){
            endHour = 0;
        }
        else if(!endAM && endHour != 12) endHour += 12;
        TimeOfDay endTime = new TimeOfDay(endHour, (int) endMinuteBox.getValue());
        int year = jDatePicker.getModel().getYear();
        int month = jDatePicker.getModel().getMonth();
        int day = jDatePicker.getModel().getDay();
        int dayOfWeek = new GregorianCalendar(year, month, day).get(Calendar.DAY_OF_WEEK);
        if (validateForm(startTime, endTime, dayOfWeek))
        {
            Address patientAddress = new Address(streetBox.getText(), cityBox.getText(),
                    State.fromName(stateCombo.getSelectedItem().toString()), zipBox.getValue().toString());
            Patient newPatient = new Patient(firstNameBox.getText(), lastNameBox.getText(),
                    phoneBox.getValue().toString(), patientAddress);
            GregorianCalendar start = new GregorianCalendar(year, month, day, startHour, (int) startMinuteBox.getValue());
            GregorianCalendar end = new GregorianCalendar(year, month, day, endHour, (int) endMinuteBox.getValue());
            SpecialType st = null;
            if (specialTypesCombo.getSelectedItem() != null)
            {
                st = SpecialType.fromName(specialTypesCombo.getSelectedItem().toString());
            }

            int provider_id = -1;
            Provider p = (Provider) providerCombo.getSelectedItem();
            for (Map.Entry<Integer, Provider> e : providerMap.entrySet())
            {
                if (Objects.equals(e.getValue(), p))
                {
                    provider_id = e.getKey();
                }
            }
            boolean providerAvailable = true;
            providerAvailable = checkAvailable(p, start);
            int availabilityCheckResult = -1;
            if (!providerAvailable)
            {
                availabilityCheckResult = JOptionPane.showConfirmDialog(contentPane, "Provider is not " +
                        "available at this time. Schedule anyways?", "Provider Unavailable",
                        JOptionPane.OK_CANCEL_OPTION);
            }
            if (providerAvailable || availabilityCheckResult == JOptionPane.OK_OPTION)
            {
                result = new Pair<>(new Appointment(newPatient, p, reasonBox.getText(), start, end, st, smokerCheckBox.isSelected()), provider_id);
                dialogResult = JOptionPane.OK_OPTION;
                dispose();
            }
        }
    }

    /**
     * Gets the appointment created by the dialog; null if cancelled or invalid form
     * @return the appointment
     */
    public Pair<Appointment, Integer> getResult(){
        return result;
    }

    /**
     * Event handler for cancel button
     */
    private void onCancel() {
        dialogResult = JOptionPane.CANCEL_OPTION;
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

        // Populates the provider dropdown
        DefaultComboBoxModel<Object> providerModel = new DefaultComboBoxModel<>();
        for (Provider p : providerMap.values()){
            providerModel.addElement(p);
        }
        providerCombo.setModel(providerModel);

        // Populates the state dropdown
        DefaultComboBoxModel<String> stateModel = new DefaultComboBoxModel<>(State.getNames());
        stateCombo.setModel(stateModel);
        stateCombo.setSelectedIndex(DEFAULT_STATE_INDEX);

        // Populates the special type combo box
        DefaultComboBoxModel<String> specialTypeModel = new DefaultComboBoxModel<>(SpecialType.getNames());
        specialTypeModel.addElement(null);
        specialTypesCombo.setModel(specialTypeModel);
        specialTypesCombo.setSelectedIndex(specialTypeModel.getIndexOf(null));

        // Sets the limits on the time spinners
        SpinnerNumberModel startHours = new SpinnerNumberModel(1, 1, 12, 1);
        startHourBox.setModel(startHours);
        SpinnerNumberModel endHours = new SpinnerNumberModel(1, 1, 12, 1);
        endHourBox.setModel(endHours);

        // Note: Minute values will be cleansed to 15 minute intervals
        SpinnerNumberModel startMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        startMinuteBox.setModel(startMinutes);
        startMinuteBox.setEditor(new JSpinner.NumberEditor(startMinuteBox, "00"));
        SpinnerNumberModel endMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        endMinuteBox.setModel(endMinutes);
        endMinuteBox.setEditor(new JSpinner.NumberEditor(endMinuteBox, "00"));

        final String[] pmOps = {"AM", "PM"};
        SpinnerListModel startPm = new SpinnerListModel(Arrays.asList(pmOps));
        SpinnerListModel endPm = new SpinnerListModel(Arrays.asList(pmOps));
        startPMBox.setModel(startPm);
        endPMBox.setModel(endPm);
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
            zipBox.setCaretPosition(0);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(contentPane, "Invalid ZIP");
        }

        try {
            MaskFormatter phoneFormat = new MaskFormatter("(###) ###-####");
            phoneBox = new JFormattedTextField(phoneFormat);
            phoneBox.setCaretPosition(0);
        } catch (ParseException e){
            JOptionPane.showMessageDialog(contentPane, "Invalid phone number.");
        }
    }

    /**
     * Validates the new appointment dialog form
     * @param start the start time of the appt
     * @param end the end time of the appt
     * @param dayOfWeek the day of the week for the appointment
     * @return true if input is valid; else false
     */
    private boolean validateForm(TimeOfDay start, TimeOfDay end, int dayOfWeek){
        if(firstNameBox.getText() == "" || lastNameBox.getText() == "" || streetBox.getText() == "" ||
                cityBox.getText() == "" || zipBox.getValue() == null || phoneBox.getValue() == null){
                JOptionPane.showMessageDialog(contentPane, "Missing or incorrect form information. " +
                        "Please verify all fields are filled completely.", "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if ((int)endMinuteBox.getValue() % 15 != 0 || (int)startMinuteBox.getValue() % 15 != 0){
            JOptionPane.showMessageDialog(contentPane, "Minutes must be in 15 minute intervals.",
                    "Invalid Times", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(end.beforeOrEqual(start)){
            JOptionPane.showMessageDialog(contentPane, "End time is before or equal to start time.",
                    "Invalid Times", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        double open = GlobalConfig.getInstance().getStart_time();
        int openHour = (int)open;
        int openMinute = (int)(open - openHour) * 60;
        double close = GlobalConfig.getInstance().getEnd_time();
        int closeHour = (int)close;
        int closeMinute = (int)(close - closeHour) * 60;
        TimeOfDay openTime = new TimeOfDay(openHour, openMinute);
        TimeOfDay closeTime = new TimeOfDay(closeHour, closeMinute);
        if (start.before(openTime) || start.after(closeTime) || end.before(openTime) || end.after(closeTime)
                || dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
        {
            int dialogResult = JOptionPane.showConfirmDialog(contentPane, "Appointment is outside normal business hours. Schedule anyways?", "Outside Business Hours", JOptionPane.WARNING_MESSAGE);
            if (dialogResult == JOptionPane.CANCEL_OPTION)
            {
                return false;
            }
        }
        return true;
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
     *
     * @param p
     * @param c
     * @return
     */
    private boolean checkAvailable(Provider p, GregorianCalendar c)
    {
        boolean available = false;
        for(Availability a : p.getAvailability())
        {
            if (a.duringThis(c))
            {
                available = true;
                break;
            }
        }
        return available;
    }
}
