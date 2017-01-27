package Models.Provider;

import Models.Day;
import Models.TimeOfDay;

import java.util.List;

/**
 * Stores the availability of a provider
 */
public class Availability {

    /**
     * Boolean array indicating if the availability applies to a given day; 0 for monday, 1 for tuesday,...
     * 4 for friday
     * MUST HAVE LENGTH OF EXACTLY 5
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
     * @param days Boolean array indicating if the availability applies to a given day; 0 for monday, 1 for tuesday, ...
     *             4 for friday
     *             MUST HAVE LENGTH OF EXACTLY 5
     * @param start the time of day this availability begins at
     * @param end the time of day this availability ends at
     * @param week the week_of_month this availability occurs on; 0 for every week
     */
    public Availability(boolean[] days, TimeOfDay start, TimeOfDay end, int week){
        if (week < 0 || week > 5){
            throw new IllegalArgumentException("Week must be a valid week_of_month or 0 for all weeks.");
        }
        if (days.length != 5){
            throw new IllegalArgumentException("Days array must have length 5; one index for each weekday.");
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

    public String getDisplayName()
    {
        String dayList = "";
        String weekList;
        for (int i = 0; i < days.length; i++)
        {
            if (days[i])
            {
                dayList += Day.values()[i].getCharAbbrev();
            }
        }

        switch(week)
        {
            case 0:
                weekList = "every week";
                break;
            default:
                weekList = "week " + week;
                break;
        }

        String ret = dayList + " " + weekList + " from " + start.to12String() + " to " + end.to12String();
        return ret;
    }
}
