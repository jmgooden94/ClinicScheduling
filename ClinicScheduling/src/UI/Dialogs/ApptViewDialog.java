package UI.Dialogs;

import Models.Appointment.Appointment;
import Models.Appointment.ApptStatus;
import Models.Patient.Address;
import Models.Patient.Patient;
import Utils.MySqlUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;


public class ApptViewDialog extends JDialog
{
    private ApptViewDialog self;
    private Appointment appointment;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");

    private JPanel contentPanel = new JPanel();

    private JButton okBtn;
    private JButton rescheduuleBtn;
    private JButton noShowBtn;
    private JButton cancelApptBtn;

    public ApptViewDialog(Appointment a)
    {
        self = this;
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setResizable(false);

        this.appointment = a;

        buildUI();
        this.getContentPane().add(contentPanel);
        this.pack();
        this.setVisible(true);
    }

    private void buildUI()
    {
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(new JLabel("Patient Info"));
        outerPanel.add(titlePanel);
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));

        Patient patient = appointment.getPatient();
        boxPanel.add(buildLabelFieldRow("First Name: ", patient.getFirstName()));
        boxPanel.add(buildLabelFieldRow("Last Name: ", patient.getLastName()));
        boxPanel.add(buildLabelFieldRow("Phone: ", patient.getPhone()));

        Address address = patient.getAddress();
        boxPanel.add(buildLabelFieldRow("Street: ", address.getStreet()));
        boxPanel.add(buildLabelFieldRow("City: ", address.getCity()));
        boxPanel.add(buildLabelFieldRow("State: ", address.getState().getName()));

        JPanel apptTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        apptTitlePanel.add(new JLabel("Appointment Info"));
        boxPanel.add(apptTitlePanel);

        GregorianCalendar start = appointment.getApptStart();
        GregorianCalendar end = appointment.getApptEnd();
        boxPanel.add(buildLabelFieldRow("Date: ", dateFormatter.format(start.getTime())));
        boxPanel.add(buildLabelFieldRow("Start Time: ", timeFormatter.format(start.getTime())));
        boxPanel.add(buildLabelFieldRow("End Time: ", timeFormatter.format(end.getTime())));
        boxPanel.add(buildLabelFieldRow("Provider: ", appointment.getProvider().toString()));

        Box reasonBox = Box.createHorizontalBox();
        reasonBox.add(new JLabel("Reason:"));
        JTextArea reason = new JTextArea(4, 15);
        reason.setEditable(false);
        reason.setText(appointment.getReason());
        reasonBox.add(reason);

        boxPanel.add(reasonBox);

        outerPanel.add(boxPanel);
        outerPanel.add(buildButtonPanel());
        contentPanel.add(outerPanel);
    }

    private Box buildLabelFieldRow(String label, String field)
    {
        Box b = Box.createHorizontalBox();
        b.add(new JLabel(label));
        JTextField txt = new JTextField("", 15);
        txt.setEditable(false);
        txt.setText(field);
        b.add(txt);
        return b;
    }

    private JPanel buildButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        okBtn = new JButton("OK");
        okBtn.addActionListener(okListener);
        panel.add(okBtn);

        rescheduuleBtn = new JButton("Reschedule");
        rescheduuleBtn.addActionListener(rescheduleListener);
        panel.add(rescheduuleBtn);

        noShowBtn = new JButton("No Show");
        noShowBtn.addActionListener(noShowListener);
        panel.add(noShowBtn);

        cancelApptBtn = new JButton("Cancel Appointment");
        cancelApptBtn.addActionListener(cancelListener);
        panel.add(cancelApptBtn);

        return panel;
    }

    private ActionListener okListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            self.dispose();
        }
    };

    private ActionListener rescheduleListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            new RescheduleDialog(appointment);
            //self.dispose();
        }
    };

    private ActionListener noShowListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                int result = JOptionPane.showConfirmDialog(contentPanel, "Are you sure you want to mark this appointment as a no-show?",
                        "No Show?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION)
                {
                    MySqlUtils.updateApptStatus(appointment.getId(), ApptStatus.NO_SHOW);
                    self.dispose();
                }
            }
            catch(SQLException ex)
            {
                showError("Appointment Error", "Error setting appointment status.", ex);
            }
        }
    };

    private ActionListener cancelListener = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            // NOTE: If this array changes, switch/case must also change
            String[] buttons = {"Provider Cancelled", "Patient Cancelled", "Do Not Cancel"};
            int opt = JOptionPane.showOptionDialog(contentPanel, "Did patient or provider cancel this appointment?",
                    "Cancel Appointment", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, buttons, buttons[2]);
            // NOTE: These options are based on the buttons array
            try
            {
                switch (opt)
                {
                    case 0:
                        MySqlUtils.updateApptStatus(appointment.getId(), ApptStatus.CANCELLED_BY_PROVIDER);
                        dispose();
                        break;
                    case 1:
                        MySqlUtils.updateApptStatus(appointment.getId(), ApptStatus.CANCELLED_BY_PATIENT);
                        dispose();
                        break;
                }
            }
            catch(SQLException ex)
            {
                // TODO: showError() <--- this method exists on Nic's branch
            }
        }
    };

    private void showError(String title, String msg, Exception e)
    {
        System.out.println(e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(new JFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
    }
}
