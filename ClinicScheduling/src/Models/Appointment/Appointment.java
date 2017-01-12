package Models.Appointment;

import Models.Patient.Patient;
import Models.Provider.Provider;
import Models.TimeOfDay;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

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

    private TimeOfDay startTime;

    private TimeOfDay endTime;

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
}
