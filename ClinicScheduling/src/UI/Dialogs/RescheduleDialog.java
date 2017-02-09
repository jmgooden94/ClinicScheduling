package UI.Dialogs;

import Models.Appointment.Appointment;
import Models.Appointment.ApptStatus;
import Utils.MySqlUtils;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Created by nicjohnson on 2/6/17.
 */
public class RescheduleDialog extends JDialog
{
    private JPanel contentPanel = new JPanel();

    private JDatePickerImpl datePicker;

    private JSpinner startHour;
    private JSpinner startMinute;
    private JComboBox<String> startAmPm;

    private JSpinner endHour;
    private JSpinner endMinute;
    private JComboBox<String> endAmPm;

    private JComboBox<String> canceledByBox;

    private JButton okBtn;
    private JButton cancelBtn;

    private Appointment appointment;

    public RescheduleDialog(Appointment a)
    {
        this.appointment = a;

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        buildUI();

        this.getContentPane().add(contentPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setTitle("Reschedule Appointment");
        this.setVisible(true);
    }

    private void buildUI()
    {
        Box dateBox = Box.createHorizontalBox();
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {

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

        dateBox.add(new JLabel("New Date: "));
        dateBox.add(datePicker);

        contentPanel.add(dateBox);

        contentPanel.add(buildStartTimeRow());
        contentPanel.add(buildEndTimeRow());

        Box typeBox = Box.createHorizontalBox();
        canceledByBox = new JComboBox<>();
        canceledByBox.addItem("Provider");
        canceledByBox.addItem("Patient");

        typeBox.add(new JLabel("Canceled By: "));
        typeBox.add(canceledByBox);

        contentPanel.add(typeBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okBtn = new JButton("OK");
        okBtn.addActionListener(okListener);

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(cancelListener);

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        contentPanel.add(buttonPanel);
    }

    private Box buildStartTimeRow()
    {
        Box b = Box.createHorizontalBox();

        b.add(new JLabel("Start Time:"));

        SpinnerNumberModel startHours = new SpinnerNumberModel(1, 1, 12, 1);
        startHour = new JSpinner(startHours);

        SpinnerNumberModel startMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        startMinute = new JSpinner(startMinutes);
        startMinute.setEditor(new JSpinner.NumberEditor(startMinute, "00"));

        startAmPm = new JComboBox<>();
        startAmPm.addItem("AM");
        startAmPm.addItem("PM");

        b.add(startHour);
        b.add(startMinute);
        b.add(startAmPm);

        return b;
    }

    private Box buildEndTimeRow()
    {
        Box b = Box.createHorizontalBox();

        b.add(new JLabel("End Time:"));

        SpinnerNumberModel endHours = new SpinnerNumberModel(1, 1, 12, 1);
        endHour = new JSpinner(endHours);

        SpinnerNumberModel endMinutes = new SpinnerNumberModel(0, 0, 59, 15);
        endMinute = new JSpinner(endMinutes);
        endMinute.setEditor(new JSpinner.NumberEditor(startMinute, "00"));

        endAmPm = new JComboBox<>();
        endAmPm.addItem("AM");
        endAmPm.addItem("PM");

        b.add(endHour);
        b.add(endMinute);
        b.add(endAmPm);

        return b;
    }

    private boolean isFormValid()
    {
        if ((int)startHour.getValue() < 1 || (int)startHour.getValue() > 12)
        {
            popDialog("Start hour must be between 1 and 12");
            return false;
        }
        if ((int)endHour.getValue() < 1 || (int)endHour.getValue() > 12)
        {
            popDialog("End hour must be between 1 and 12");
            return false;
        }
        if ((int)startMinute.getValue() < 0 || (int)startMinute.getValue() > 59)
        {
            popDialog("Start minute must be between 0 and 59");
            return false;
        }
        if ((int)endMinute.getValue() < 0 || (int)endMinute.getValue() > 59)
        {
            popDialog("End minute must be between 0 and 59");
            return false;
        }
        return true;
    }

    private void popDialog(String msg)
    {
        JOptionPane.showMessageDialog(this, msg,
                "Form Error", JOptionPane.ERROR_MESSAGE);
    }

    private ActionListener okListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isFormValid())
            {
                try
                {
                    if ((canceledByBox.getSelectedItem()).equals("Provider"))
                    {
                        MySqlUtils.updateApptStatus(appointment.getId(), ApptStatus.RESCHEDULED_BY_PROVIDER);
                    }
                    else
                    {
                        MySqlUtils.updateApptStatus(appointment.getId(), ApptStatus.RESCHEDULED_BY_PATIENT);
                    }
                }
                catch(SQLException ex)
                {
                    showError("Error Rescheduling", "Error rescheduling appointment", ex);
                }

                int year = datePicker.getModel().getYear();
                int month = datePicker.getModel().getMonth();
                int day = datePicker.getModel().getDay();

                int startHourVal = (int)startHour.getValue();
                if (startAmPm.getSelectedItem().equals("PM"))
                {
                    startHourVal += 12;
                }

                int endHourVal = (int)endHour.getValue();
                if (endAmPm.getSelectedItem().equals("PM"))
                {
                    endHourVal += 12;
                }

                GregorianCalendar newStart = new GregorianCalendar(year, month, day,
                        startHourVal, (int)startMinute.getValue());
                GregorianCalendar newEnd = new GregorianCalendar(year, month, day,
                        endHourVal, (int)endMinute.getValue());

                try
                {
                    Appointment newAppt = new Appointment(appointment.getPatient(),
                            appointment.getProvider(), appointment.getReason(),
                            newStart, newEnd, appointment.getSpecialType(),
                            appointment.getSmoker());

                    MySqlUtils.addAppointment(newAppt, newAppt.getProvider().getId());
                }
                catch(SQLException ex)
                {
                    showError("Error Rescheduling Appointment", "Error rescheduling appointment", ex);
                }
                dispose();
            }
        }
    };

    private ActionListener cancelListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };

    private void showError(String title, String msg, Exception e)
    {
        System.out.println(e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(new JFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
    }

}
