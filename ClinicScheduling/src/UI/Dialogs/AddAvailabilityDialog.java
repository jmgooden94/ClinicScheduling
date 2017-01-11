package UI.Dialogs;

import Models.Day;
import Models.Provider.Availability;
import Models.Provider.Recurrence;
import Models.Provider.WeekOfMonthRecurrence;
import Models.TimeOfDay;

import javax.swing.*;
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
    private JSpinner recurrenceSpinner;
    private Availability result;

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

        String[] recurOps = {"0 - Every Week", "1 - First Week", "2 - Second Week", "3 - Third Week", "4 - Fourth Week",
                "5 - Fifth Week"};
        SpinnerListModel recurModel = new SpinnerListModel(recurOps);
        recurrenceSpinner.setModel(recurModel);

        setVisible(true);
    }

    private void onOK() {
        int recurOption = Integer.parseInt(recurrenceSpinner.getValue().toString().substring(0, 1));
        Recurrence r = new WeekOfMonthRecurrence(recurOption);

        List<Day> days = new ArrayList<>();
        if (mondayCheckBox.isSelected()){days.add(Day.MONDAY);}
        if (tuesdayCheckBox.isSelected()){days.add(Day.TUESDAY);}
        if (wednesdayCheckBox.isSelected()){days.add(Day.WEDNESDAY);}
        if (thursdayCheckBox.isSelected()){days.add(Day.THURSDAY);}
        if (fridayCheckBox.isSelected()){days.add(Day.FRIDAY);}

        int startHour = (int) startHourSpinner.getValue();
        boolean startAM = startPMSpinner.getValue().toString().equals("AM");
        if(startAM && startHour == 12){
            startHour = 0;
        }
        else if (!startAM) startHour += 12;
        TimeOfDay start = new TimeOfDay(startHour, (int) startMinuteSpinner.getValue());
        int endHour = (int) endHourSpinner.getValue();
        boolean endAM = endPMSpinner.getValue().toString().equals("AM");
        if(endAM && endHour == 12){
            endHour = 0;
        }
        else if(!endAM) endHour += 12;
        TimeOfDay end = new TimeOfDay(endHour, (int) endMinuteSpinner.getValue());

        if(validateForm(start, end)){
            result = new Availability(r, days, start, end);
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
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
