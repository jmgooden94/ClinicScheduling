package Models.Appointment;

import Models.Patient.Patient;
import Models.Provider.Provider;
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
    /**
     * The special type of this appointment; this field can be null
     */
    private SpecialType specialType;

    /**
     * Constructs a new appointment
     * @param patient The patient this appointment is for
     * @param provider The provider serving this appointment
     * @param reason The reason for the appointment
     * @param apptStart The start of the appointment
     * @param apptEnd The end of the appointment
     * @param specialType The special type of this appointment, or null
     */
    public Appointment(Patient patient, Provider provider, String reason, GregorianCalendar apptStart, GregorianCalendar apptEnd, SpecialType specialType) {
        if (patient == null || provider == null || apptStart == null || apptEnd == null){
            throw new IllegalArgumentException("patient, provider, apptStart, and apptEnd cannot be null");
        }
        this.patient = patient;
        this.provider = provider;
        this.reason = reason;
        this.apptStart = apptStart;
        this.apptEnd = apptEnd;
        this.specialType = specialType;
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
}
