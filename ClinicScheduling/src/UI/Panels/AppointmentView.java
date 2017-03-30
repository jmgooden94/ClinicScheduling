package UI.Panels;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Models.Appointment.Appointment;
import Models.TimeOfDay;
import UI.Dialogs.ApptViewDialog;
import UI.MainView;
import Utils.*;

import java.awt.*;
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
	private MainView parent;
	private List<String> columnNames;

	public AppointmentView(List<Appointment> appointments, MainView parent)
	{
		this.parent = parent;
		timeList = this.createTimes();
		if (appointments != null)
		{
			this.appointments = new ArrayList<>();
			this.appointments.add(new ArrayList<>());
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
		JTable table = new JTable(this)
		{
			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		table.setFont(GlobalConfig.CALENDAR_PANEL_FONT);

		table.setCellSelectionEnabled(true);
		ListSelectionModel cellSelectionModel = table.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
				{
					int selectedRow = table.getSelectedRow();
					int selectedColumn = table.getSelectedColumn();

					//System.out.printf("CELL SELECTED AT [%d, %d]\n", selectedRow, selectedColumn);

					if (objArray[selectedColumn][selectedRow] != null)
					{
						Appointment_ColoredDataCell cell = (Appointment_ColoredDataCell) objArray[selectedColumn][selectedRow];
						Appointment a = cell.getAppointment();
						new ApptViewDialog(a);
						parent.updateApptView();
					}
				}
			}

		});

		//table.setTableHeader(null);
		table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));

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
	 * Groups the appointments by doctor
	 */
	protected void organize()
	{
		List<Appointment> temp = new ArrayList<>();
		temp.addAll(this.appointments.get(0));

		List<List<Appointment>> byProvider = new ArrayList<>();

		temp.sort(new Comparator<Appointment>()
		{
			@Override
			public int compare(Appointment o1, Appointment o2)
			{
				return Integer.compare(o1.getProvider().getId(), o2.getProvider().getId());
			}

		});

		if (temp.size() > 0)
		{
			byProvider.add(new ArrayList<>());
			byProvider.get(0).add(temp.get(0));
			int currId = temp.get(0).getProvider().getId();
			int byIndex = 0;

			int size = temp.size();
			for (int i = 1; i < size; i++)
			{
				if (temp.get(i).getProvider().getId() != currId)
				{
					byProvider.add(new ArrayList<>());
					byIndex++;
					currId = temp.get(i).getProvider().getId();
				}
				byProvider.get(byIndex).add(temp.get(i));
			}
		}

		this.columnNames = new ArrayList<>();
		this.columnNames.add("Time");
		this.appointments.clear();

		for (int i = 0; i < byProvider.size(); i++)
		{
			this.appointments.add(new ArrayList<>());
			this.appointments.get(i).addAll(byProvider.get(i));
			this.columnNames.add(byProvider.get(i).get(0).getProvider().getName());
		}

		this.columnCount = byProvider.size() + 1;

//		for (int i = 0; i < byProvider.size(); i++)
//		{
//			for (int j = 0; j < byProvider.get(i).size(); j++)
//			{
//				System.out.println(byProvider.get(i).get(j).testMethod());
//			}
//			System.out.println("=-=-=-=-=-");
//		}
	}

	// This was the original implementation of organize, which tried to condense the
	// appointments into as few columns as possible.
//	/**
//	 * Orangizes the appointments in the list so that overlapping
//	 * appointments get moved to the next column.
//	 *
//	 * If the list when
//	 *      A
//	 *      B
//	 *      C
//	 * And A and B overlapped in time, then after this method it would be
//	 *      A   C
//	 *      B
//	 * Where C is now in it's own list.
//	 *
//	 * It also sets the number of columns that are going to be
//	 * needed in the table
//	 */
//	protected void organize()
//	{
//		int outer = 0;
//
//		// Loop through the top level lists, which
//		// correspond to columns
//		while (outer < this.appointments.size())
//		{
//			// If there's only 1 thing in this list then we're
//			// done here
//			if (this.appointments.get(outer).size() <= 1)
//			{
//				break;
//			}
//
//			Appointment a;
//			Appointment b;
//
//			int i = 0;
//
//			// Loop through the items in the column, moving them to the next column
//			// as necessary
//			while (i < this.appointments.get(outer).size() - 1)
//			{
//				a = this.appointments.get(outer).get(i);
//				b = this.appointments.get(outer).get(i + 1);
//
//				// A flag to make sure that if two appointments after both
//				// overlap a, then they both get moved.
//				boolean moved = false;
//
//				// If the start time of the next appointment is
//				// earlier than the end time of the first appointment,
//				// then they overlap
//				if (b.getStartTime().compareTo(a.getEndTime()) < 0)
//				{
//					// Remove b from the array list and catch it
//					b = this.appointments.get(outer).remove(i + 1);
//
//					// If there isn't a list after this to put b in
//					// create one
//					if (outer == this.appointments.size() - 1)
//					{
//						this.appointments.add(new ArrayList<Appointment>());
//					}
//					// Add b to the end of the list
//					this.appointments.get(outer + 1).add(b);
//
//					moved = true;
//				}
//				// Only increment if nothing moved.
//				if (!moved)
//				{
//					i++;
//				}
//			}
//
//			outer++;
//		}
//
//		// After you're done looping, you now know how many
//		// columns the table will need, set it now.
//		//
//		// Adding 1 is for the time column
//		this.columnCount = this.appointments.size() + 1;
//	}

	@Override
	public String getColumnName(int index)
	{
		return this.columnNames.get(index);
	}

	/**
	 * Builds a 2D array of ColoredDataCell objects which
	 * have the data to be displayed as well as info about
	 * the color which should be rendered.
	 * @return 2D array of ColoredDataCell
	 *
	 * WHY DOESN'T THIS BULLSHIT WORK????
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
						//System.out.println(a.testMethod() + " is during " + t.to12String());
						obj[col + 1][i] = new Appointment_ColoredDataCell(a.displayString(), a.getColor(), a);
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
