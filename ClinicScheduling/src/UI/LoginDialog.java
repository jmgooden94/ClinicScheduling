package UI;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

import Utils.*;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameBox;
    private JPasswordField passwordBox;
    private boolean login;
    private int dialogResult = -1;

    public LoginDialog(boolean login) {
        this.login = login;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.setTitle("Clinic Scheduler Login");

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

        // set dialog size and center
        setSize(400, 200);
        setLocationRelativeTo(null);
    }

    private void onOK()
    {
        UserRole role = null;
        String un = usernameBox.getText().trim();
        char[] plain = passwordBox.getPassword();
        String plainString = new String(plain);
        //boolean success = MySqlUtils.openConnection(un, plainString);
        boolean success = MySqlUtils.openConnection("clinic_admin", "password");
        if(success){
            try {
                //role = MySqlUtils.getRole(un);
                role = MySqlUtils.getRole("clinic_admin");
            }
            catch (SQLException sqle){
                showError(sqle);
            }
            dispose();
            if(login)
            {
                new MainView(role);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(contentPane, "Unable to connect to database. Check your credentials "
                    + "and/or verify that server is running.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        dialogResult = JOptionPane.OK_OPTION;
    }

    private void onCancel() {
        dialogResult = JOptionPane.CANCEL_OPTION;
        dispose();
        if(login)
        {
            System.exit(0);
        }
    }

    /**
     * Shows an error dialog with the exception message
     * @param ex the exception to display
     */
    private void showError(Exception ex){
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
    }

    public int showDialog()
    {
        this.pack();
        this.setVisible(true);
        return dialogResult;
    }

    public static void main(String[] args) {
        new LoginDialog(true).showDialog();
    }

}
