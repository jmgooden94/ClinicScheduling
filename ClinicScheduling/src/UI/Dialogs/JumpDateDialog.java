package UI.Dialogs;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

public class JumpDateDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel mainPanel;
    private int dialogResult = -1;
    private GregorianCalendar selected;
    private JDatePickerImpl jDatePicker;

    public JumpDateDialog()
    {
        setContentPane(contentPane);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(300, 200));
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        createDatePickerPanel();

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public int showDialog()
    {
        setVisible(true);
        return dialogResult;
    }

    public GregorianCalendar getSelected()
    {
        return selected;
    }

    private void onOK()
    {
        dialogResult = JOptionPane.OK_OPTION;
        int year = jDatePicker.getModel().getYear();
        int month = jDatePicker.getModel().getMonth();
        int day = jDatePicker.getModel().getDay();
        selected = new GregorianCalendar(year, month, day, 00, 00, 00);
        dispose();
    }

    private void onCancel()
    {
        dialogResult = JOptionPane.CANCEL_OPTION;
        dispose();
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
            public Object stringToValue(String text) throws ParseException
            {
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
        mainPanel.add(jDatePicker);
    }
}
