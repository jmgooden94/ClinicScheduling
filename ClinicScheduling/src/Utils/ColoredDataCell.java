package Utils;

import java.awt.Color;

public class ColoredDataCell
{
    /**
     * The background color of the cell
     */
    private Color color;

    /**
     * The data to be displayed in the cell.
     */
    private String data;

    public ColoredDataCell(String data, Color color)
    {
        this.data = data;
        this.color = color;
    }

    public String getData()
    {
        return this.data;
    }

    public Color getColor()
    {
        return this.color;
    }
}
