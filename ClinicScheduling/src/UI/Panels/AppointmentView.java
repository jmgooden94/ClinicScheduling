package UI.Panels;

import javax.swing.*;
import Models.Appointment.Appointment;
import Models.TimeOfDay;
import Utils.ColoredDataCell;
import Utils.AppointmentCellRenderer;

import java.awt.*;
import java.sql.Time;
import java.util.*;
import java.util.List;

/**
 * Monthly calendar view panel
 */
public class AppointmentView extends MultiColumnView
{
	/**
	 * A list of lists representing all appointments organized
	 * in their proper columns. Each top level list is a column
	 * with no overlapping appointments.
	 */
	private List<ArrayList<Appointment>> appointments;

	public AppointmentView(List<Appointment> appointments)
	{

		timeList = this.createTimes();
		if (appointments != null)
		{
			this.appointments = new ArrayList<ArrayList<Appointment>>();
			this.appointments.add(new ArrayList<Appointment>());
			this.appointments.get(0).addAll(appointments);

			this.appointments.get(0).sort(Comparator.comparing(Appointment::getStartTime));

			organize();
			assignColors();
			this.objArray = buildTableObject();

//            System.out.println("ORGANIZED\n");
//            for (int i = 0; i < this.appointments.size(); i++)
//            {
//                for (int j = 0; j < this.appointments.get(i).size(); j++)
//                {
//                    System.out.println(this.appointments.get(i).get(j).getStartTime() + " - " +
//							this.appointments.get(i).get(j).getEndTime());
//                }
//                System.out.println("=-=-=-=-=-");
//            }
		}
	}


	/**
	 * Creates the JTable for rendering in the MainView,
	 * so the main method is super clean
	 *
	 * @return A JTable representing this day
	 */
	public JScrollPane getView() {
		JTable table = new JTable(this);
		table.setTableHeader(null);

		// These two get grid lines to show up on Mac
		table.setGridColor(Color.black);
		table.setShowGrid(true);

		// So the time column isn't equal sized as the rest.
		table.getColumnModel().getColumn(0).setMaxWidth(75);

		for (int i = 1; i < this.columnCount; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(new AppointmentCellRenderer());
		}

		JScrollPane sp = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return sp;
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
	protected void organize()
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
				if (b.getStartTime().compareTo(a.getEndTime()) < 0)
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

	/**
	 * Builds a 2D array of ColoredDataCell objects which
	 * have the data to be displayed as well as info about
	 * the color which should be rendered.
	 * @return 2D array of ColoredDataCell
	 */
	protected ColoredDataCell[][] buildTableObject()
	{
		ColoredDataCell[][] obj = new ColoredDataCell[this.columnCount][this.ROW_COUNT];
		int apt_size = this.appointments.size();
		int time_size = this.timeList.size();
		for (int col = 0; col < apt_size; col++)
		{
			int col_size = this.appointments.get(col).size();
			TimeOfDay t;
			for (int i = 0; i < time_size; i++)
			{
				t = this.timeList.get(i);
				for (int j = 0; j < col_size; j++)
				{
					Appointment a = this.appointments.get(col).get(j);

					if (a.during(t))
					{
						obj[col + 1][i] = new ColoredDataCell(a.displayString(), a.getColor());
					}
				}
			}
		}
		return obj;
	}

	/**
	 * Assigns colors to appointments, alternating between two colors in one column,
	 * and then two different colors in the next column, that way
	 * no two appointments next to each other will ever have the same color
	 *
	 * Hopefully....
	 */
	protected void assignColors()
	{
		int appt_size = this.appointments.size();
		int column = 0;
		for (int i = 0; i < appt_size; i++)
		{
			int apt = 0;
			int col_size = this.appointments.get(i).size();
			for (int j = 0; j < col_size; j++)
			{
				this.appointments.get(i).get(j).setColor(this.cellColors[column][apt]);

				apt = apt == 0 ? 1 : 0;
			}
			column = column == 0 ? 1 : 0;
		}
	}

}
