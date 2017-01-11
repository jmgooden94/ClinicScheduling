// Some code in this class is copied from or based off code by Marty Strep of The University of Washington
package UI.Panels;

import Models.Appointment.Appointment;
import Models.Day;
import Models.TimeOfDay;

import javax.swing.table.AbstractTableModel;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Weekly agenda-style view
 */
public class ProviderView extends AbstractTableModel{
    /**
     * The start hour for the table; 15 minutes before first available appointment time
     */
    private static int START_HOUR = 9;
    /**
     * number of rows in the table
     */
    private static int ROW_COUNT = 38;
    /**
     * number of columns; 5 days/week + label column
     */
    private static int COLUMN_COUNT = 6;
    /**
     * List of appointments
     */
    private List<Appointment> appointments;
    /**
     * Start date for week
     */
    private GregorianCalendar startDate;

    /**
     * Constructs a new ProviderView showing the given date
     * @param appointments the appointment list
     * @param startDate the date to include
     */
    public ProviderView(List<Appointment> appointments, GregorianCalendar startDate){
        this.appointments = appointments;
        this.startDate = startDate;
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
            return time.to12String();   // a time label in the leftmost column
        }

        Day day = Day.toDay(columnIndex);
        // TODO: this
        return null;
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

    // returns the Time that corresponds to the given row
    private static TimeOfDay toTime(int row) {
        // every 2 columns correspond to one row
        row++;
        int hour = START_HOUR + row / 4;
        int minute = 15 * (row % 4);

        TimeOfDay time = new TimeOfDay(hour, minute);
        return time;
    }
}