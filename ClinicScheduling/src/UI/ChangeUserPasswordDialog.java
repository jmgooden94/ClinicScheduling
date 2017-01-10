package UI;

import Utils.MySqlUtils;
import Utils.UserRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class ChangeUserPasswordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField pwBox;
    private JPasswordField confirmPwBox;
    private JSpinner usernameSpinner;

    public ChangeUserPasswordDialog() {
        SpinnerListModel roles = new SpinnerListModel(UserRole.values());

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

        SpinnerListModel usernames = null;
        try{
            usernames = new SpinnerListModel(MySqlUtils.getUsernames());
        }
        catch(SQLException ex){
            showError(ex);
        }
        usernameSpinner.setModel(usernames);

        setVisible(true);
    }

    private void onOK() {
        String un = usernameSpinner.getValue().toString();
        char[] pwChar = pwBox.getPassword();
        String pwPlain = new String(pwChar);
        char[] verifyPwChar = confirmPwBox.getPassword();
        String verifyPlain = new String(verifyPwChar);
        if (!pwPlain.equals("") && !un.equals("")) {
            if (pwPlain.equals(verifyPlain)) {
                try {
                    MySqlUtils.changePassword(un, pwPlain);
                    JOptionPane.showMessageDialog(contentPane, "Password changed.");
                    dispose();
                } catch (SQLException sqle) {
                        showError(sqle);
                }
            }
            else {
                JOptionPane.showMessageDialog(contentPane, "Passwords don't match. Please verify your password.");
            }
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
