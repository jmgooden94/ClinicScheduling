package UI.Panels;


import Models.Appointment.Appointment;
import Models.Provider.Provider;
import Models.TimeOfDay;
import Utils.ColoredDataCell;
import Utils.ProviderCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AllProviderView extends MultiColumnView
{
    private List<ArrayList<Provider>> providers;

    public AllProviderView(List<Provider> _providers)
    {
        timeList = this.createTimes();
        this.providers = new ArrayList<>();
        this.providers.add(new ArrayList<>());
        this.providers.get(0).addAll(_providers);

        this.providers.get(0).sort(Comparator.comparing(Provider::getStart));

        organize();
        assignColors();
        this.objArray = buildTableObject();

//        System.out.println("ORGANIZED\n");
//        for (int i = 0; i < this.providers.size(); i++)
//        {
//            for (int j = 0; j < this.providers.get(i).size(); j++)
//            {
//                System.out.println(this.providers.get(i).get(j).getName());
//            }
//            System.out.println("=-=-=-=-=-");
//        }
    }

    protected void organize()
    {
        int outer = 0;

        // Loop through the top level lists, which
        // correspond to columns
        while (outer < this.providers.size())
        {
            // If there's only 1 thing in this list then we're
            // done here
            if (this.providers.get(outer).size() <= 1)
            {
                break;
            }

            Provider a;
            Provider b;

            int i = 0;

            // Loop through the items in the column, moving them to the next column
            // as necessary
            while (i < this.providers.get(outer).size() - 1)
            {
                a = this.providers.get(outer).get(i);
                b = this.providers.get(outer).get(i + 1);

                // A flag to make sure that if two appointments after both
                // overlap a, then they both get moved.
                boolean moved = false;

                // If the end time of the first provider is
                // earlier than the start time of the next provider,
                // then they overlap
                if (b.getStart().compareTo(a.getEnd()) < 0)
                {
                    // Remove b from the array list and catch it
                    b = this.providers.get(outer).remove(i + 1);

                    // If there isn't a list after this to put b in
                    // create one
                    if (outer == this.providers.size() - 1)
                    {
                        this.providers.add(new ArrayList<Provider>());
                    }
                    // Add b to the end of the list
                    this.providers.get(outer + 1).add(b);

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
        this.columnCount = this.providers.size() + 1;
    }

    protected ColoredDataCell[][] buildTableObject()
    {
        ColoredDataCell[][] obj = new ColoredDataCell[this.columnCount][this.ROW_COUNT];
        int apt_size = this.providers.size();
        int time_size = this.timeList.size();
        for (int col = 0; col < apt_size; col++)
        {
            int col_size = this.providers.get(col).size();
            TimeOfDay t;
            for (int i = 0; i < time_size; i++)
            {
                t = this.timeList.get(i);
                for (int j = 0; j < col_size; j++)
                {
                    Provider a = this.providers.get(col).get(j);

                    if (a.during(t))
                    {
                        obj[col + 1][i] = new ColoredDataCell(a.getName(), a.getColor());
                    }
                }
            }
        }
        return obj;
    }

    protected void assignColors()
    {
        int appt_size = this.providers.size();
        int column = 0;
        for (int i = 0; i < appt_size; i++)
        {
            int apt = 0;
            int col_size = this.providers.get(i).size();
            for (int j = 0; j < col_size; j++)
            {
                this.providers.get(i).get(j).setColor(this.cellColors[column][apt]);

                apt = apt == 0 ? 1 : 0;
            }
            column = column == 0 ? 1 : 0;
        }
    }

    public JTable getView()
    {
        JTable table = new JTable(this);

        // These two get grid lines to show up on Mac
        table.setGridColor(Color.black);
        table.setShowGrid(true);

        // So the time column isn't equal sized as the rest.
        table.getColumnModel().getColumn(0).setMaxWidth(75);

        for (int i = 1; i < this.columnCount; i++)
        {
            table.getColumnModel().getColumn(i).setCellRenderer(new ProviderCellRenderer());
        }

        return table;
    }
}
