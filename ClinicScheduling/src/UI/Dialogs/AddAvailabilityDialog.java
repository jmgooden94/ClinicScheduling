package UI.Dialogs;

import Models.Day;
import Models.Provider.Availability;
import Models.TimeOfDay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAvailabilityDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox mondayCheckBox;
    private JCheckBox tuesdayCheckBox;
    private JCheckBox wednesdayCheckBox;
    private JCheckBox thursdayCheckBox;
    private JCheckBox fridayCheckBox;
    private JSpinner startHourSpinner;
    private JSpinner startMinuteSpinner;
    private JSpinner startPMSpinner;
    private JSpinner endHourSpinner;
    private JSpinner endMinuteSpinner;
    private JSpinner endPMSpinner;
    private JComboBox recurrenceCombo;
    private Availability result;
    /**
     * The length of the work week; MUST MATCH THE LENGTH OF THE DAYS ARRAY IN Availability.java
     */
    private static final int WEEK_LENGTH = 5;
    /**
     * Result code to be returned by the dialog on show
     */
    private int dialogResult = -1;

    public AddAvailabilityDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(400,300);
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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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

        SpinnerNumberModel startHours = new SpinnerNumberModel(1, 1, 12, 1);
        startHourSpinner.setModel(startHours);
        SpinnerNumberModel endHours = new SpinnerNumberModel(1, 1, 12, 1);
        endHourSpinner.setModel(endHours);

        SpinnerNumberModel startMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        startMinuteSpinner.setModel(startMinutes);
        startMinuteSpinner.setEditor(new JSpinner.NumberEditor(startMinuteSpinner, "00"));
        SpinnerNumberModel endMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        endMinuteSpinner.setModel(endMinutes);
        endMinuteSpinner.setEditor(new JSpinner.NumberEditor(endMinuteSpinner, "00"));

        String[] pmOps = {"AM", "PM"};
        SpinnerListModel startPm = new SpinnerListModel(Arrays.asList(pmOps));
        SpinnerListModel endPm = new SpinnerListModel(Arrays.asList(pmOps));
        startPMSpinner.setModel(startPm);
        JComponent startPMeditor = startPMSpinner.getEditor();
        if(startPMeditor instanceof JSpinner.DefaultEditor){
            JSpinner.DefaultEditor pmSpinnerEditor = (JSpinner.DefaultEditor)startPMeditor;
            pmSpinnerEditor.getTextField().setHorizontalAlignment(JTextField.RIGHT);
        }
        endPMSpinner.setModel(endPm);
        startPMSpinner.setModel(startPm);
        JComponent endPMeditor = endPMSpinner.getEditor();
        if(startPMeditor instanceof JSpinner.DefaultEditor){
            JSpinner.DefaultEditor pmSpinnerEditor = (JSpinner.DefaultEditor)endPMeditor;
            pmSpinnerEditor.getTextField().setHorizontalAlignment(JTextField.RIGHT);
        }

        final String[] recurOps = {"Every Week", "First Week", "Second Week", "Third Week", "Fourth Week",
                "Fifth Week"};
        DefaultComboBoxModel<String> recurModel = new DefaultComboBoxModel<>(recurOps);
        recurrenceCombo.setModel(recurModel);
    }

    public int showDialog(){
        setVisible(true);
        return dialogResult;
    }

    private void onOK() {
        int week = recurrenceCombo.getSelectedIndex();

        boolean[] days = new boolean[WEEK_LENGTH];
        days[0] = mondayCheckBox.isSelected();
        days[1] = tuesdayCheckBox.isSelected();
        days[2] = wednesdayCheckBox.isSelected();
        days[3] = thursdayCheckBox.isSelected();
        days[4] = fridayCheckBox.isSelected();

        // Convert 12 hour time format shown on GUI into 24 hour format used by calendar class
        int startHour = (int) startHourSpinner.getValue();
        boolean startAM = startPMSpinner.getValue().toString().compareTo("AM") == 0;
        if(startAM && startHour == 12){
            startHour = 0;
        }
        else if (!startAM && startHour != 12) startHour += 12;
        TimeOfDay start = new TimeOfDay(startHour, (int) startMinuteSpinner.getValue());
        int endHour = (int) endHourSpinner.getValue();
        boolean endAM = endPMSpinner.getValue().toString().equals("AM");
        if(endAM && endHour == 12){
            endHour = 0;
        }
        else if(!endAM && endHour != 12) endHour += 12;
        TimeOfDay end = new TimeOfDay(endHour, (int) endMinuteSpinner.getValue());

        if(validateForm(start, end)){
            result = new Availability(days, start, end, week);
            dispose();
        }
        dialogResult = JOptionPane.OK_OPTION;
    }

    private void onCancel() {
        // add your code here if necessary
        dialogResult = JOptionPane.CANCEL_OPTION;
        dispose();
    }

    /**
     * Returns the availability created by the form; null if canceled
     * @return the availability created by the form; null if canceled
     */
    public Availability getResult(){
        return result;
    }

    /**
     * Verifies the form information is valid
     * @param start the start time from the form
     * @param end the end time from the form
     * @return true if valid; else false
     */
    private boolean validateForm(TimeOfDay start, TimeOfDay end){
        if (!mondayCheckBox.isSelected() && !tuesdayCheckBox.isSelected() && !wednesdayCheckBox.isSelected() &&
                !thursdayCheckBox.isSelected() && !fridayCheckBox.isSelected()){
            JOptionPane.showMessageDialog(contentPane, "Must select at least one day.", "Missing Day",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (end.beforeOrEqual(start)){
            JOptionPane.showMessageDialog(contentPane, "End time is before or equal to start time.",
                    "Invalid Times", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
