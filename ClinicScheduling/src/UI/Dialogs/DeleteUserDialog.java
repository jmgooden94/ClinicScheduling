package UI.Dialogs;

import Utils.MySqlUtils;

import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;

public class DeleteUserDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner usernameSpinner;
    private int dialogResult = -1;

    public DeleteUserDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(300,200);
        setLocationRelativeTo(null);

        SpinnerListModel usernames = null;
        try{
             usernames = new SpinnerListModel(MySqlUtils.getUsernames());
        }
        catch(SQLException ex){
            showError(ex);
        }
        usernameSpinner.setModel(usernames);

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
    }

    public int showDialog(){
        setVisible(true);
        return dialogResult;
    }


    private void onOK() {
        try{
            MySqlUtils.removeUser(usernameSpinner.getValue().toString());
            JOptionPane.showMessageDialog(new JFrame(), "User Deleted.");
            dialogResult = JOptionPane.OK_OPTION;
            dispose();
        }
        catch (SQLException sqlex){
            showError(sqlex);
        }
        catch (IllegalArgumentException iae){
            JOptionPane.showMessageDialog(new JFrame(), "You may not delete yourself or 'clinic_admin'", "Illegal Argument", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dialogResult = JOptionPane.CANCEL_OPTION;
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
