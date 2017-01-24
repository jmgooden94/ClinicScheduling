package Utils;

import Models.Appointment.Appointment;
import UI.Panels.AppointmentView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by nicjohnson on 1/12/17.
 */
public class AppointmentCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        AppointmentView tableModel = (AppointmentView) table.getModel();
        if (tableModel.getValueAt(row, col) != null)
        {
            l.setBackground(tableModel.getCellColor(row, col));
        }
        else
        {
            l.setBackground(AppointmentView.DEFAULT_COLOR);
        }
        Color bg = l.getBackground();
        if (bg == Color.blue || bg == Color.red)
        {
            l.setForeground(Color.white);
        }

        //Return the JLabel which renders the cell.
        return l;
    }
}
