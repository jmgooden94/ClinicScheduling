package UI.Dialogs;


import Models.Provider.Availability;
import Models.Provider.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        contentPanel.add(outer);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        okBtn = new JButton("OK");
        okBtn.addActionListener(disposeListener);
        okBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonPanel.add(okBtn);
        contentPanel.add(buttonPanel);
    }

    private ActionListener addListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO: pop add availability dialog and add
            System.out.println("ADD CLICKED");
        }
    };

    private ActionListener deleteListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: call the MySQLUtils delete availability
            int index = Integer.parseInt(e.getActionCommand());
            System.out.println("DELETE INDEX " + index);
        }
    };

    private ActionListener disposeListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            self.dispose();
        }
    };
}
