package Utils;

import Models.Appointment.Appointment;

import java.awt.*;

/**
 * Created by nicjohnson on 2/3/17.
 */
public class Appointment_ColoredDataCell extends ColoredDataCell
{
    private Appointment appointment;

    public Appointment_ColoredDataCell(String s, Color c, Appointment a)
    {
        super(s, c);
        this.appointment = a;
    }

    public Appointment getAppointment()
    {
        return this.appointment;
    }
}
