package UI;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;

import Utils.*;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameBox;
    private JPasswordField passwordBox;

    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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

    private void onOK() {
        String un = usernameBox.getText().trim();
        char[] plain = passwordBox.getPassword();
        String plainString = new String(plain);
        Connection connection = MySqlUtils.openConnection(un, plainString);
        if(connection != null){
            dispose();

            new MainView("Admin");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

}
