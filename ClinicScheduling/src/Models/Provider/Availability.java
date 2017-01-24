package Models.Provider;

import Models.Day;
import Models.TimeOfDay;

import java.util.List;

/**
 * Stores the availability of a provider
 */
public class Availability {

    /**
     * When this availability recurs
     */
    private Recurrence recurrence;
    /**
     * The days this availability recurs on
     */
    private List<Day> days;
    /**
     * The beginning of this availability
     */
    private TimeOfDay start;
    /**
     * The end of this availability
     */
    private TimeOfDay end;

    /**
     * Constructs a new Availability with recurrence
     * @param recurrence when this availability recurs
     * @param days the day(s) of the week this availability occurs on
     * @param start the time of day this availability begins at
     * @param end the time of day this availability ends at
     */
    public Availability(Recurrence recurrence, List<Day> days, TimeOfDay start, TimeOfDay end){
        this.recurrence = recurrence;
        this.days = days;
        this.start = start;
        this.end = end;
    }

    public Recurrence getRecurrence(){
        return recurrence;
    }

    public TimeOfDay getStart(){
        return start;
    }

    public TimeOfDay getEnd(){
        return end;
    }

    public List<Day> getDays(){
        return days;
    }
}
