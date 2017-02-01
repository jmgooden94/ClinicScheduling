package UI.Dialogs;

import Utils.MySqlUtils;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;
import Models.Appointment.SpecialType;

public class GetStatsDialog extends JDialog
{
    private JPanel contentPanel = new JPanel();
    private JDatePickerImpl start_date;
    private JDatePickerImpl end_date;
    private JButton getStatsBtn;
    /**
     * I would put all the JLabels here for all the special
     * types of appointments but looping through them makes adding
     * new appointment types way easier
     */

    public GetStatsDialog()
    {
        contentPanel.setLayout(new GridLayout(0, 1));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        buildUI();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(contentPanel, BorderLayout.CENTER);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

    private void buildUI()
    {
        JPanel datePanel = buildPickerPanel();
        contentPanel.add(datePanel);
        contentPanel.updateUI();
    }

    private JPanel buildPickerPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        UtilDateModel start_model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl start_datePanel = new JDatePanelImpl(start_model, p);
        start_date = new JDatePickerImpl(start_datePanel, new JFormattedTextField.AbstractFormatter() {

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
        panel.add(new JLabel("Start Date:"));
        panel.add(start_date);

        UtilDateModel end_model = new UtilDateModel();
        JDatePanelImpl end_datePanel = new JDatePanelImpl(end_model, p);
        end_date = new JDatePickerImpl(end_datePanel, new JFormattedTextField.AbstractFormatter() {

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
        panel.add(new JLabel("End Date:"));
        panel.add(end_date);

        panel.add(Box.createVerticalStrut(5));

        getStatsBtn = new JButton("Get Statistics");
        getStatsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int startYear = start_date.getModel().getYear();
                int startMonth = start_date.getModel().getMonth();
                int startDay = start_date.getModel().getDay();

                int endYear = end_date.getModel().getYear();
                int endMonth = end_date.getModel().getMonth();
                int endDay = end_date.getModel().getDay();

                GregorianCalendar start = new GregorianCalendar(startYear, startMonth, startDay,
                                                        0, 0, 0);
                GregorianCalendar end = new GregorianCalendar(endYear, endMonth, endDay,
                                                    23, 59, 59);

                JPanel types = buildSpecialTypes(start, end);
                contentPanel.add(types);
                contentPanel.updateUI();
            }
        });
        panel.add(getStatsBtn);

        return panel;
    }

    private JPanel buildSpecialTypes(GregorianCalendar start, GregorianCalendar end)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel data = new JPanel();
        data.setLayout(new BoxLayout(data, BoxLayout.Y_AXIS));

        data.add(new JLabel("Number of appointments of type: "));

        HashMap<String, Integer> specialMap = MySqlUtils.getSpecialTypeCounts(start, end);

        for (String key : specialMap.keySet())
        {
            data.add(new JLabel("        " + SpecialType.valueOf(key).getName() + ": " + specialMap.get(key)));
        }



        JPanel dataContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dataContainer.add(data);

        JScrollPane sp = new JScrollPane(dataContainer, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(sp);

        return panel;
    }
}
