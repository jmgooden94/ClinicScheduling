package UI.Panels;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import Models.Appointment.Appointment;
import Models.TimeOfDay;

import java.awt.*;
import java.sql.Time;
import java.util.*;
import java.util.List;

/**
 * View for a single day of appointments
 */
public class AppointmentView extends AbstractTableModel{

    /**
     * Time appointments start, corresponds to 9:30am
     */
    private final double START_TIME = 9.5;

    /**
     * Time appointments end, corresponds to 6:15pm
     */
    private final double END_TIME = 18.25;

    /**
     * The length of an appointment, corresponds to
     * 15 minutes
     */
    private final double APPOINTMENT_LENGTH = 0.25;

    /**
     * Number of rows, (end - start) * 4, for a row every 15 minutes
     */
    // TODO: why do you need to add 1 here to make the last appt display?
    private final int ROW_COUNT = (int) (((END_TIME - START_TIME) * 4) + 1);

    /**
     * List of all appointment times throughout the
     * day
     */
    private List<TimeOfDay> list;

    /**
     * Starting column count, can be modified if
     * appointments overlap
     */
    private int columnCount = 2;


    public AppointmentView()
    {
        list = createTimes();
    }

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
        return list;
    }

    /**
     * Create a list of TimeOfDay objects based
     * on the start and end times of the
     * day
     * @return ArrayList with 1 TimeOfDay object every 15 minutes
     */
    private List<TimeOfDay> createTimes()
    {
        List<TimeOfDay> list = new ArrayList<TimeOfDay>();
        TimeOfDay tod;
        for (double i = START_TIME; i <= END_TIME; i += APPOINTMENT_LENGTH)
        {
            int hour = (int) Math.floor(i);
            double frac = i - hour;
            boolean pm = false;
            if (hour >= 12) {
                pm = true;
                if (hour > 12) {
                    hour -= 12;
                }
            }
            int minute = (int) (frac * 60);
            tod = new TimeOfDay(hour, minute, pm);
            list.add(tod);
        }
        return list;
    }

    /**
     * Creates the JTable for rendering in the MainView,
     * so the main method is super clean
     *
     * @return A JTable representing this day
     */
    public JTable getDayView()
    {
        JTable table = new JTable(this);

        // These two get grid lines to show up on Mac
        table.setGridColor(Color.black);
        table.setShowGrid(true);

        // So the time column isn't equal sized as the rest.
        table.getColumnModel().getColumn(0).setMaxWidth(75);

        return table;
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
            return list.get(rowIndex).toString();
        }
        else
        {
            return null;
        }
    }
}
