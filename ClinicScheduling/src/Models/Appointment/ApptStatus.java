package Models.Appointment;

/**
 * Created by Jonathan on 1/31/2017.
 */
public enum ApptStatus {
    CANCELLED_BY_PATIENT ("Cancelled by Patient"),
    CANCELLED_BY_PROVIDER ("Cancelled by Provider"),
    RESCHEDULED_BY_PATIENT ("Rescheduled by Patient"),
    RESCHEDULED_BY_PROVIDER ("Rescheduled by Provider"),
    NO_SHOW ("No Show");

    String name;

    ApptStatus(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
