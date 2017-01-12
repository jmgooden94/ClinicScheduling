package UI.Dialogs;

import Models.Provider.Availability;
import Models.Provider.Provider;
import Models.Provider.ProviderType;
import Utils.MySqlUtils;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
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
    private JSpinner typeSpinner;
    private JButton addAvailabilityButton;
    private JPanel availabilityPanel;
    private List<Availability> availabilities = new ArrayList<>();

    public AddProviderDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(400, 200);
        setLocationRelativeTo(null);

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

        SpinnerListModel types = new SpinnerListModel(ProviderType.getNames());
        typeSpinner.setModel(types);

        setVisible(true);
    }

    private void onOK() {
        if (validateForm()){
            String fn = firstNameField.getText();
            fn = fn.substring(0,1).toUpperCase() + fn.substring(1);
            String ln = firstNameField.getText();
            ln = ln.substring(0,1).toUpperCase() + ln.substring(1);
            ProviderType pt = ProviderType.fromName(typeSpinner.getValue().toString());
            Provider p = new Provider(pt, fn, ln, availabilities);
            try{
                MySqlUtils.addProvider(p);
            }
            catch (SQLException ex){
                showError(ex);
            }
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onAddAvailability(){
        Availability fromDialog = new AddAvailabilityDialog().getResult();
        if(fromDialog != null){
            availabilities.add(fromDialog);
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
}
