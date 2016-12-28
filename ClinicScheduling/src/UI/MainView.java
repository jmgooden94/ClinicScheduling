// Some code in this class is copied from or based off code by Marty Strep of The University of Washington
package UI;

import Models.Day;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

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
        createWeekView();
    }

    private void createWeekView(){
        AbstractTableModel model = new WeekView(null);
        JTable scheduleTable = new JTable(model);

        // set up the table column headings
        JTableHeader header = scheduleTable.getTableHeader();
        header.setReorderingAllowed(false);
        TableColumnModel columnModel = scheduleTable.getColumnModel();
        for (int c = 0; c < model.getColumnCount(); c++) {
            TableColumn column = columnModel.getColumn(c);
            Day day = WeekView.toDay(c);
            column.setHeaderValue(day == null ? "" : String.valueOf(day));
        }
        ListSelectionModel selectionModel = scheduleTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarPanel.add(scheduleTable.getTableHeader(), BorderLayout.NORTH);
        calendarPanel.add(scheduleTable, BorderLayout.CENTER);
    }
}
