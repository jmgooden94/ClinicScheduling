package UI;

import javax.swing.*;

public class MainView extends JFrame {

    private JButton leftArrowButton;
    private JButton rightArrowButton;
    private JButton importButton;
    private JButton exportButton;
    private JButton newApptButton;
    private JLabel dateLabel;
    private JButton newProvButton;
    private JButton toggleViewButton;
    private JPanel contentPane;
    private JLabel providerLabel;
    private JPanel providerPanel;
    private JPanel calendarPanel;
    private JPanel leftPanel;

    public MainView(){
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(null);
        setContentPane(contentPane);
        setVisible(true);
    }
}
