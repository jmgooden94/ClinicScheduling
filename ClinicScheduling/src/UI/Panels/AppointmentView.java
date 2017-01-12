package UI.Panels;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import Models.Appointment.Appointment;
import Models.TimeOfDay;

import java.awt.*;
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
    // TODO: why do you need to add 1 here to make the last appt of the day display?
    private final int ROW_COUNT = (int) (((END_TIME - START_TIME) * 4) + 1);

    /**
     * List of all possible appointment times throughout the
     * day
     */
    private List<TimeOfDay> timeList;

    /**
     * A list of lists representing all appointments organized
     * in their proper columns. Each top level list is a column
     * with no overlapping appointments.
     */
    private List<ArrayList<Appointment>> appointments;

    /**
     * Starting column count, can be modified if
     * appointments overlap
     */
    private int columnCount = 2;


    public AppointmentView(List<Appointment> appointments)
    {
        timeList = createTimes();
        if (appointments != null)
        {
            this.appointments = new ArrayList<ArrayList<Appointment>>();
            this.appointments.add(new ArrayList<Appointment>());
            this.appointments.get(0).addAll(appointments);

            this.appointments.get(0).sort(Comparator.comparing(Appointment::getApptStart));

//            for (int i = 0; i < this.appointments.get(0).size(); i++)
//            {
//                System.out.println(this.appointments.get(0).get(i).testMethod());
//            }

            organizeAppointments();
//
//            System.out.println("ORGANIZED\n");
//            for (int i = 0; i < this.appointments.size(); i++)
//            {
//                for (int j = 0; j < this.appointments.get(i).size(); j++)
//                {
//                    System.out.println(this.appointments.get(i).get(j).testMethod());
//                }
//                System.out.println("=-=-=-=-=-");
//            }
        }
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
        return timeList;
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
     * Orangizes the appointments in the list so that overlapping
     * appointments get moved to the next column.
     *
     * If the list when
     *      A
     *      B
     *      C
     * And A and B overlapped in time, then after this method it would be
     *      A   C
     *      B
     * Where C is now in it's own list.
     *
     * It also sets the number of columns that are going to be
     * needed in the table
     */
    private void organizeAppointments()
    {
        int outer = 0;

        // Loop through the top level lists, which
        // correspond to columns
        while (outer < this.appointments.size())
        {
            // If there's only 1 thing in this list then we're
            // done here
            if (this.appointments.get(outer).size() <= 1)
            {
                break;
            }

            Appointment a;
            Appointment b;

            int i = 0;

            // Loop through the items in the column, moving them to the next column
            // as necessary
            while (i < this.appointments.get(outer).size() - 1)
            {
                a = this.appointments.get(outer).get(i);
                b = this.appointments.get(outer).get(i + 1);

                // A flag to make sure that if two appointments after both
                // overlap a, then they both get moved.
                boolean moved = false;

                // If the end time of the first appointment is
                // earlier than the start time of the next appointment,
                // then they overlap
                if (b.getApptStart().compareTo(a.getApptEnd()) <= 0)
                {
                    // Remove b from the array list and catch it
                    b = this.appointments.get(outer).remove(i + 1);

                    // If there isn't a list after this to put b in
                    // create one
                    if (outer == this.appointments.size() - 1)
                    {
                        this.appointments.add(new ArrayList<Appointment>());
                    }
                    // Add b to the end of the list
                    this.appointments.get(outer + 1).add(b);

                    moved = true;
                }
                // Only increment if nothing moved.
                if (!moved)
                {
                    i++;
                }
            }

            outer++;
        }

        // After you're done looping, you now know how many
        // columns the table will need, set it now.
        //
        // Adding 1 is for the time column
        this.columnCount = this.appointments.size() + 1;
    }

    private Object[][] buildTableObject()
    {
        Object[][] obj = new Object[this.columnCount][this.ROW_COUNT];

        return null;
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
            return timeList.get(rowIndex).toString();
        }
        else
        {
            return null;
        }
    }
}
