package UI.Dialogs;

import Models.Day;
import Models.Provider.Availability;
import Models.Provider.Recurrence;
import Models.Provider.WeekOfMonthRecurrence;
import Models.TimeOfDay;
import sun.security.provider.ConfigFile;

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

    public AddAvailabilityDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(400,200);
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
        endPMSpinner.setModel(endPm);

        String[] recurOps = {"0 - Every Week", "1 - First Week", "2 - Second Week", "3 - Third Week", "4 - Fourth Week",
                "5 - Fifth Week"};
        SpinnerListModel recurModel = new SpinnerListModel(recurOps);
        recurrenceSpinner.setModel(recurModel);

        setVisible(true);
    }

    private void onOK() {
        int recurOption = (int) recurrenceSpinner.getValue().toString().charAt(0);
        Recurrence r = new WeekOfMonthRecurrence(recurOption);

        List<Day> days = new ArrayList<>();
        if (mondayCheckBox.isSelected()){days.add(Day.MONDAY);}
        if (tuesdayCheckBox.isSelected()){days.add(Day.TUESDAY);}
        if (wednesdayCheckBox.isSelected()){days.add(Day.WEDNESDAY);}
        if (thursdayCheckBox.isSelected()){days.add(Day.THURSDAY);}
        if (fridayCheckBox.isSelected()){days.add(Day.FRIDAY);}

        TimeOfDay start = new TimeOfDay((int) startHourSpinner.getValue(), (int) startMinuteSpinner.getValue(),
                (startPMSpinner.getValue().toString().equals("PM")));
        TimeOfDay end = new TimeOfDay((int) endHourSpinner.getValue(), (int) endMinuteSpinner.getValue(),
                (endPMSpinner.getValue().toString().equals("PM")));

        Availability availability = new Availability(r, days, start, end);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
