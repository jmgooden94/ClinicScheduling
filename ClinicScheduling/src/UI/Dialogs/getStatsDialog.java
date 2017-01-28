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
import java.util.Properties;

public class getStatsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JPanel startDatePanel;
    private JPanel endDatePanel;
    private JButton getStatsButton;
    private int dialogResult = -1;
    private JDatePickerImpl jDatePickerStart;
    private JDatePickerImpl jDatePickerEnd;

    public getStatsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600,600));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

        jDatePickerStart = initalizeDatePicker(jDatePickerStart);
        jDatePickerEnd = initalizeDatePicker(jDatePickerEnd);
        startDatePanel.add(jDatePickerStart, BorderLayout.EAST);
        endDatePanel.add(jDatePickerEnd, BorderLayout.EAST);
    }

    public int showDialog(){
        setVisible(true);
        return dialogResult;
    }

    private void onOK() {
        // add your code here
        dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dialogResult = JOptionPane.CANCEL_OPTION;
        dispose();
    }

    /**
     * Initializes a new JDatePickerImpl
     */
    private JDatePickerImpl initalizeDatePicker(JDatePickerImpl j) {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        j = new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {

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
        return j;
    }
}
