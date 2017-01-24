package Utils;

import UI.Panels.AllProviderView;
import UI.Panels.MultiColumnView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by nicjohnson on 1/18/17.
 */
public class ProviderCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        AllProviderView tableModel = (AllProviderView) table.getModel();
        if (tableModel.getValueAt(row, col) != null)
        {
            l.setBackground(tableModel.getCellColor(row, col));
        }
        else
        {
            l.setBackground(MultiColumnView.DEFAULT_COLOR);
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
