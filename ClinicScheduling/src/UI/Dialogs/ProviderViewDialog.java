package UI.Dialogs;


import Models.Provider.Availability;
import Models.Provider.Provider;
import Utils.MySqlUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProviderViewDialog extends JDialog
{
    private ProviderViewDialog self;
    private JPanel contentPanel = new JPanel();
    private JTextField firstName;
    private JTextField lastName;
    private JTextField providerType;
    private JButton addAvailabilityBtn;
    private JButton okBtn;
    private Provider provider;
    private Box outer;

    public ProviderViewDialog(Provider p)
    {
        this.setModal(true);
        self = this;
        provider = p;
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        buildUI();
        this.getContentPane().add(contentPanel);
        this.pack();
        this.setVisible(true);
    }

    private void buildUI()
    {
        Box firstNameBox = Box.createHorizontalBox();
        firstNameBox.add(new JLabel("First Name: "));
        firstName = new JTextField(provider.getFirstName(), 15);
        firstName.setEditable(false);
        firstNameBox.add(firstName);
        contentPanel.add(firstNameBox);

        Box lastNameBox = Box.createHorizontalBox();
        lastNameBox.add(new JLabel("Last Name: "));
        lastName = new JTextField(provider.getLastName(), 15);
        lastName.setEditable(false);
        lastNameBox.add(lastName);
        contentPanel.add(lastNameBox);

        Box typeBox = Box.createHorizontalBox();
        typeBox.add(new JLabel("Type: "));
        providerType = new JTextField(provider.getProviderType().getName(), 15);
        providerType.setEditable(false);
        typeBox.add(providerType);
        contentPanel.add(typeBox);

        Box availabilityBtnBox = Box.createHorizontalBox();
        availabilityBtnBox.add(new JLabel("Availability: "));
        addAvailabilityBtn = new JButton("Add Availability");
        addAvailabilityBtn.addActionListener(addListener);
        availabilityBtnBox.add(addAvailabilityBtn);
        contentPanel.add(availabilityBtnBox);

        outer = Box.createHorizontalBox();
        displayAvailabilites();
        contentPanel.add(outer);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        okBtn = new JButton("OK");
        okBtn.addActionListener(disposeListener);
        okBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonPanel.add(okBtn);
        contentPanel.add(buttonPanel);
    }

    private void displayAvailabilites()
    {
        if (provider.getAvailability() != null)
        {
            List<Availability> availList = provider.getAvailability();
            for (int i = 0; i < availList.size(); i++)
            {
                Box b = Box.createHorizontalBox();
                JButton button = new JButton("Delete");
                button.setActionCommand(Integer.toString(i));
                button.addActionListener(deleteListener);
                b.add(button);
                b.add(new JLabel(availList.get(i).getDisplayName()));
                outer.add(b);
            }
        }
    }

    private ActionListener addListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try
            {
                AddAvailabilityDialog d = new AddAvailabilityDialog();
                if(d.showDialog() == JOptionPane.OK_OPTION)
                {
                    Availability fromDialog = d.getResult();
                    int id = MySqlUtils.addSingleAvailability(provider.getId(), fromDialog);
                    fromDialog.setId(id);
                    provider.getAvailability().add(fromDialog);
                    Box b = Box.createHorizontalBox();
                    JButton button = new JButton("Delete");
                    button.setActionCommand(Integer.toString(id));
                    button.addActionListener(deleteListener);
                    b.add(button);
                    b.add(new JLabel(fromDialog.getDisplayName()));
                    outer.add(b);
                    outer.updateUI();
                    self.pack();
                }
            }
            catch(SQLException ex)
            {
                showError("Unexpected Error", "Cannot add availability to database.", ex);
            }
        }
    };

    private ActionListener deleteListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
           try
           {
               int index = Integer.parseInt(e.getActionCommand());
               MySqlUtils.deleteAvailability(provider.getAvailability().get(index).getId());
               provider.getAvailability().remove(index);
               outer.removeAll();
               displayAvailabilites();
               contentPanel.revalidate();
               contentPanel.updateUI();
           }
           catch (SQLException ex)
           {
               showError("Unexpected Error", "Error deleting availability.", ex);
           }
        }
    };

    private ActionListener disposeListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            self.dispose();
        }
    };

    private void showError(String title, String msg, Exception e)
    {
        System.out.println(e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(new JFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
    }
}
