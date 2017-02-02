package UI.Dialogs;

import Models.Provider.Availability;
import Models.Provider.Provider;
import Models.Provider.ProviderType;
import UI.MainView;
import Utils.MySqlUtils;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddProviderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JButton addAvailabilityButton;
    private JPanel availabilityPanel;
    private JComboBox typeSpinner;
    private List<Availability> availabilities = new ArrayList<>();
    private MainView mainView;
    private int dialogResult = -1;
    private Provider result;

    /**
     * Constructor for add provider dialog
     */
    public AddProviderDialog() {
        this.mainView = mainView;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);

        availabilityPanel.setLayout(new GridLayout(0, 1));

        // Sets document filters to limit length of inputs
        AbstractDocument fnDoc = (AbstractDocument) firstNameField.getDocument();
        fnDoc.setDocumentFilter(new Utils.DocumentSizeFilter(100));

        AbstractDocument lnDoc = (AbstractDocument) lastNameField.getDocument();
        lnDoc.setDocumentFilter(new Utils.DocumentSizeFilter(100));

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

        addAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddAvailability();
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

        List<String> list = Models.Provider.ProviderType.getNames();
        String[] values = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            values[i] = list.get(i);
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(values);

        typeSpinner.setModel(model);
        this.pack();
    }

    public int showDialog(){
        setVisible(true);
        return dialogResult;
    }

    private void onOK() {
        if (validateForm()){
            String fn = firstNameField.getText();
            fn = fn.substring(0,1).toUpperCase() + fn.substring(1);
            String ln = lastNameField.getText();
            ln = ln.substring(0,1).toUpperCase() + ln.substring(1);
            ProviderType pt = ProviderType.fromName((String)typeSpinner.getSelectedItem());
            Provider p = new Provider(pt, fn, ln, availabilities);
            try{
                MySqlUtils.addProvider(p);
            }
            catch (SQLException ex){
                showError(ex);
            }
            result = p;
            dialogResult = JOptionPane.OK_OPTION;
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dialogResult = JOptionPane.CANCEL_OPTION;
        dispose();
    }

    private void onAddAvailability(){
        AddAvailabilityDialog d = new AddAvailabilityDialog();
        if(d.showDialog() == JOptionPane.OK_OPTION)
        {
            Availability fromDialog = d.getResult();
            availabilities.add(fromDialog);
            availabilityPanel.add(new JLabel(fromDialog.getDisplayName()));
            availabilityPanel.updateUI();
            this.pack();
        }
    }

    /**
     * Verifies that all values on the add provider dialog are valid
     * @return true if valid; else false;
     */
    private boolean validateForm(){
        if(firstNameField.getText().equals("") || lastNameField.getText().equals("")){
            JOptionPane.showMessageDialog(contentPane, "Missing form information. " +
                    "Please verify all fields are filled completely.", "Missing Form Information",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else if(availabilities.size() <= 0){
            JOptionPane.showMessageDialog(contentPane, "Missing availability. " +
                    "Must add at least one availability.", "Missing Availability", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else return true;
    }

    /**
     * Shows an error dialog with the exception message
     * @param ex the exception to display
     */
    private void showError(Exception ex){
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
    }

    public Provider getResult(){ return result; }

}
