// Some code in this class is copied from or based off code by Marty Strep of The University of Washington
package UI;

import Models.Day;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setLayout(null);
        setContentPane(contentPane);
        setVisible(true);
        createWeekView();
    }

    private void createWeekView(){
        AbstractTableModel model = new WeekView(null);
        JTable scheduleTable = new JTable(model);

        // set up the table column headings
        JTableHeader header = scheduleTable.getTableHeader();
        header.setReorderingAllowed(false);
        TableColumnModel columnModel = scheduleTable.getColumnModel();
        columnModel.getColumn(0).setHeaderValue(null);
        for (int c = 1; c < model.getColumnCount(); c++) {
            TableColumn column = columnModel.getColumn(c);
            Day day = Day.toDay(c);
            column.setHeaderValue(day == null ? "" : String.valueOf(day.toString()));
        }
        ListSelectionModel selectionModel = scheduleTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarPanel.add(header, BorderLayout.NORTH);
        calendarPanel.add(scheduleTable, BorderLayout.CENTER);
    }

    private void onCancel(){
        dispose();
        System.exit(0);
    }
}
