package Models.Appointment;

import Models.Patient.Patient;
import Models.Provider.Provider;
import Models.TimeOfDay;
import UI.Panels.AppointmentView;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Stores an appointment
 */
public class Appointment {
    /**
     * The patient this appointment is for
     */
    private Patient patient;
    /**
     * The provider serving this appointment
     */
    private Provider provider;
    /**
     * The type of appointment
     */
    private String apptType;
    /**
     * The reason for the appointment
     */
    private String reason;
    /**
     * The start of the appointment
     */
    private GregorianCalendar apptStart;
    /**
     * The end of the appointment
     */
    private GregorianCalendar apptEnd;

    /**
     * Start of appointment, but only time, not a date too
     */
    private final TimeOfDay startTime;

    /**
     * End of appointment, but only time, not a date too
     */
    private final TimeOfDay endTime;

    /**
     * The color the cells corresponding to this appointment
     * are to be rendered.
     */
    private Color color = AppointmentView.DEFAULT_COLOR;

    public int test = -1;

    /**
     * Constructs a new appointment
     * @param patient The patient this appointment is for
     * @param provider The provider serving this appointment
     * @param reason The reason for the appointment
     * @param apptStart The start of the appointment
     * @param apptEnd The end of the appointment
     */
    public Appointment(Patient patient, Provider provider, String reason, GregorianCalendar apptStart, GregorianCalendar apptEnd) {
        this.patient = patient;
        this.provider = provider;
        //TODO: uncomment this once we get providers figured out
        //this.apptType = provider.getProviderType().toString();
        this.reason = reason;
        this.apptStart = apptStart;
        this.apptEnd = apptEnd;

        this.startTime = new TimeOfDay(this.apptStart.get(Calendar.HOUR_OF_DAY),
                                        this.apptStart.get(Calendar.MINUTE));
        this.endTime = new TimeOfDay(this.apptEnd.get(Calendar.HOUR_OF_DAY),
                                        this.apptEnd.get(Calendar.MINUTE));
    }

    public Patient getPatient(){
        return patient;
    }

    public Provider getProvider(){
        return provider;
    }

    public String reason(){
        return reason;
    }

    public GregorianCalendar getApptStart(){
        return apptStart;
    }

    public GregorianCalendar getApptEnd(){
        return apptEnd;
    }

    public TimeOfDay getStartTime()
    {
        return startTime;
    }

    public TimeOfDay getEndTime()
    {
        return endTime;
    }

    public Color getColor() { return this.color; }

    public void setColor(Color c) { this.color = c; }

    public void setTest(int i)
    {
        this.test = i;
    }

    public boolean during(TimeOfDay t)
    {
        return this.startTime.beforeOrEqual(t) && this.endTime.after(t);
    }

    public String testMethod()
    {
        SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String s = f.format(this.apptStart.getTime());
        String e = f.format(this.apptEnd.getTime());
        return s + " - " + e;
    }

    public String displayString()
    {
        return patient.toString() + " - " + provider.getName();
    }

    public String testDisplay()
    {
        return "Test " + this.test + " - Dr. Test";
    }
}
