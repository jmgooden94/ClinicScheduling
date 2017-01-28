package Models.Provider;

import Models.Day;
import Models.TimeOfDay;

import java.util.List;

/**
 * Stores the availability of a provider
 */
public class Availability {
    /**
     * The length of the work week; MUST MATCH THE LENGTH OF THE DAYS ARRAY IN Availability.java
     */
    // TODO: replace this with global config
    private static final int WEEK_LENGTH = 7;

    /**
     * Boolean array indicating if the availability applies to a given day; 0 for sunday, 1 for monday, ...
     * 6 for saturday
     * MUST HAVE LENGTH OF EXACTLY WEEK_LENGTH
     */
    private boolean[] days;
    /**
     * The beginning of this availability
     */
    private TimeOfDay start;
    /**
     * The end of this availability
     */
    private TimeOfDay end;
    /**
     * The week_of_month this availability occurs on; 0 for every week
     */
    private int week;

    /**
     * Constructs a new Availability
     * @param days Boolean array indicating if the availability applies to a given day; 0 for sunday, 1 for monday, ...
     * 6           for saturday
     *             MUST HAVE LENGTH OF EXACTLY 7
     * @param start the time of day this availability begins at
     * @param end the time of day this availability ends at
     * @param week the week_of_month this availability occurs on; 0 for every week
     */
    public Availability(boolean[] days, TimeOfDay start, TimeOfDay end, int week){
        if (week < 0 || week > 5){
            throw new IllegalArgumentException("Week must be a valid week_of_month or 0 for all weeks.");
        }
        if (days.length != WEEK_LENGTH){
            throw new IllegalArgumentException("Days array must have length " + WEEK_LENGTH + "; one index for each weekday.");
        }
        this.days = days;
        this.start = start;
        this.end = end;
        this.week = week;
    }

    public boolean[] getDays() {return days; }

    public TimeOfDay getStart(){
        return start;
    }

    public TimeOfDay getEnd(){
        return end;
    }

    public int getWeek() { return week; };
}
