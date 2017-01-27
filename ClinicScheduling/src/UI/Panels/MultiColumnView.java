package UI.Panels;


import Models.TimeOfDay;
import Utils.ColoredDataCell;
import Utils.GlobalConfig;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;



public abstract class MultiColumnView extends AbstractTableModel
{
    private GlobalConfig config = GlobalConfig.getInstance();
    /**
     * Time appointments start, corresponds to 9:30am
     */
    protected final double START_TIME = config.getStart_time();

    /**
     * Time appointments end, corresponds to 6:15pm
     */
    protected final double END_TIME = config.getEnd_time();

    /**
     * The length of an appointment, corresponds to
     * 15 minutes
     */
    protected final double APPOINTMENT_LENGTH = 0.25;

    /**
     * Number of rows, (end - start) * 4, for a row every 15 minutes
     */
    // TODO: why do you need to add 1 here to make the last appt of the day display?
    // TODO: the 4 should really be 1/APPOINTMENT_LENGTH but it's good enough for now
    protected final int ROW_COUNT = (int) (((END_TIME - START_TIME) * 4) + 1);

    /**
     * List of all possible appointment times throughout the
     * day
     */
    protected List<TimeOfDay> timeList;

    /**
     * 2D array of cell contents, used for rendering
     */
    protected ColoredDataCell[][] objArray;

    /**
     * Starting column count, can be modified if
     * appointments overlap
     */
    protected int columnCount = 2;

    /**
     * Default color for a non-appointment
     * cell
     */
    public static final Color DEFAULT_COLOR = Color.white;

    /**
     * Finalized array of possible colors
     */
    protected final Color [][] cellColors = {
            {Color.blue, Color.red},
            {Color.green, Color.yellow}
    };

    /**
     * Setter for column count
     * @param val value
     */
    public void setColumnCount(int val)
    {
        columnCount = val;
    }

    /**
     * Getter for the appointment times
     * @return ArrayList containing times
     */
    public List<TimeOfDay> getTimeList()
    {
        return timeList;
    }

    /**
     * Create a list of TimeOfDay objects based
     * on the start and end times of the
     * day
     * @return ArrayList with 1 TimeOfDay object every 15 minutes
     */
    protected List<TimeOfDay> createTimes()
    {
        List<TimeOfDay> list = new ArrayList<TimeOfDay>();
        TimeOfDay tod;
        for (double i = START_TIME; i <= END_TIME; i += APPOINTMENT_LENGTH)
        {
            // Get the integer part of the number
            // eg 8.75 will yield 8
            int hour = (int) Math.floor(i);
            // Get the fraction part, eg 8.75
            // will yeild 0.75
            double frac = i - hour;

            // Convert percentage to minutes, eg.
            // 0.75 will yeild 45
            int minute = (int) (frac * 60);

            // make the time of day and add it to the list
            tod = new TimeOfDay(hour, minute);
            list.add(tod);
        }
        return list;
    }

    /**
     * Abstract method to force sub classes to implement
     * their own version of organize that sorts the elements
     * of their 2D list such that no overlapping elements
     * are in the same list.
     */
    protected abstract void organize();

    /**
     * Forces sub class to implement:
     *
     * Builds a 2D array of ColoredDataCell objects which
     * have the data to be displayed as well as info about
     * the color which should be rendered.
     * @return 2D array of ColoredDataCell
     */
    protected abstract ColoredDataCell[][] buildTableObject();

    /**
     * Forces sub class to implement:
     *
     * Assigns colors to objects, alternating between two colors in one column,
     * and then two different colors in the next column, that way
     * no two objects next to each other will ever have the same color
     *
     * Hopefully....
     */
    protected abstract void assignColors();

    /**
     * Forces sub class to implement a method
     * which returns a JTable representing the
     * data contained
     * @return JTable representing class
     */
    public abstract JTable getView();

    /**
     * Gets the color that each cell should be rendered
     * @param row The row the cell is in
     * @param col The column the cell is in
     * @return The color of the cell
     */
    public Color getCellColor(int row, int col)
    {
        if (col == 0)
        {
            return this.DEFAULT_COLOR;
        }
        else
        {
            if (this.objArray[col][row] != null)
            {
                return this.objArray[col][row].getColor();
            }
            else
            {
                return this.DEFAULT_COLOR;
            }
        }
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount()
    {
        return ROW_COUNT;
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
        {
            return timeList.get(rowIndex).to12String();
        }
        else
        {
            if (this.objArray[columnIndex][rowIndex] != null)
            {
                return this.objArray[columnIndex][rowIndex].getData();
            }
            else
            {
                return null;
            }
        }
    }
}
