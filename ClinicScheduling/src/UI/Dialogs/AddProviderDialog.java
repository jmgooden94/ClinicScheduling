package UI.Dialogs;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.event.*;

public class AddProviderDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JSpinner typeSpinner;
    private JButton setAvailabilityButton;
    private JPanel availabilityPanel;

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
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
