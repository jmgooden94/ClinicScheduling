package UI;

import Utils.DocumentSizeFilter;
import Utils.MySqlUtils;
import Utils.UserRole;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.event.*;
import java.sql.SQLException;

public class AddUserDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField usernameBox;
    private JPasswordField pwBox;
    private JPasswordField confirmPwBox;
    private JSpinner roleSpinner;

    public AddUserDialog() {

        AbstractDocument unDoc = (AbstractDocument) usernameBox.getDocument();
        unDoc.setDocumentFilter(new DocumentSizeFilter(30));

        SpinnerListModel roles = new SpinnerListModel(UserRole.values());
        roleSpinner.setModel(roles);

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

        setVisible(true);
    }

    private void onOK() {
        String un = usernameBox.getText();
        char[] pwChar = pwBox.getPassword();
        String pwPlain = new String(pwChar);
        char[] verifyPwChar = confirmPwBox.getPassword();
        String verifyPlain = new String(verifyPwChar);
        if (!pwPlain.equals("") && !un.equals("")) {
            if (pwPlain.equals(verifyPlain)) {
                String roleString = roleSpinner.getValue().toString();
                UserRole role = UserRole.valueOf(roleString);
                try {
                    MySqlUtils.addUser(un, pwPlain, role);
                    JOptionPane.showMessageDialog(contentPane, "User added.\nUsername: " + un + "\nRole: " + role.toString(), "User Added", JOptionPane.PLAIN_MESSAGE);
                    dispose();
                } catch (SQLException sqle) {
                    if (sqle.getMessage().contains("Duplicate entry")) {
                        JOptionPane.showMessageDialog(new JFrame(), "User already exists. Please choose another " +
                                "username", "Duplicate User", JOptionPane.WARNING_MESSAGE);
                    } else {
                        showError(sqle);
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(contentPane, "Passwords don't match. Please verify your password.");
            }
        }
        else {
            JOptionPane.showMessageDialog(contentPane, "All fields are required.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    /**
     * Shows an error dialog with the exception message
     * @param ex the exception to display
     */
    private void showError(Exception ex){
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
    }
}
