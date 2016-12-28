// Some code in this class is copied from or based off code by Marty Strep of The University of Washington
package UI;

import Models.Appointment.Appointment;
import Models.Day;
import Models.TimeOfDay;

import javax.swing.table.AbstractTableModel;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Weekly agenda-style view
 */
public class WeekView extends AbstractTableModel{
    /**
     * The start hour for the table; 30 minutes before first available appointment time
     */
    private static int START_HOUR = 9;
    /**
     * number of rows in the table
     */
    private static int ROW_COUNT = 24;
    /**
     * number of columns; 5 days/week + label column
     */
    private static int COLUMN_COUNT = 6;
    /**
     * List of appointments
     */
    private List<Appointment> appointments;

    public WeekView(List<Appointment> appointments){
        this.appointments = appointments;
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
    public int getRowCount() {
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
        return COLUMN_COUNT;
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        checkRow(rowIndex);
        checkColumn(columnIndex);

        TimeOfDay time = toTime(rowIndex);
        if (columnIndex == 0) {
            return time;   // a time label in the leftmost column
        }

        Day day = toDay(columnIndex);
        return 0;
    }

    // A helper that throws an IllegalArgumentException if the given
    // column is outside the range of this table model.
    private void checkColumn(int column) {
        if (column < 0 || column >= getColumnCount()) {
            throw new IllegalArgumentException("column out of range: " + column);
        }
    }

    // A helper that throws an IllegalArgumentException if the given
    // row is outside the range of this table model.
    private void checkRow(int row) {
        if (row < 0 || row >= getRowCount()) {
            throw new IllegalArgumentException("row out of range: " + row);
        }
    }

    /**
     * Returns the day of the week corresponding to the given column in the table.
     * The first column (0) is the header column; the second (1) is Monday, the
     * third (2) is Tuesday, and so on.
     * @param column the 0-based column to examine
     * @return the corresponding weekday, from MONDAY to FRIDAY
     */
    public static Day toDay(int column) {
        if (column < 0 || column >= COLUMN_COUNT) {
            throw new IllegalArgumentException("column out of range: " + column);
        }
        if (column == 0) {
            return null;
        } else {
            return Day.values()[column];
        }
    }

    // returns the Time that corresponds to the given row
    private static TimeOfDay toTime(int row) {
        // every 2 columns correspond to one row
        row++;
        int hour = START_HOUR + row / 2;
        int minute = 30 * (row % 2);

        // determine AM vs. PM based on 12-hour clock
        boolean pm = false;
        if (hour >= 12) {
            pm = true;
            if (hour > 12) {
                hour -= 12;
            }
        }

        TimeOfDay time = new TimeOfDay(hour, minute, pm);
        return time;
    }
}