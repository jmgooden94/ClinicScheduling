package Models.Provider;

import Models.Day;
import Models.TimeOfDay;
import Utils.GlobalConfig;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Stores the availability of a provider
 */
public class Availability {

    private static final int FIRST_DAY_OF_WEEK = Day.MONDAY.getDayOfWeek();

    private static final int LAST_DAY_OF_WEEK = Day.FRIDAY.getDayOfWeek();

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
     * This object's id in the database
     */
    private int id;

    /**
     * Constructs a new Availability
     * @param days Boolean array indicating if the availability applies to a given day; 0 for sunday, 1 for monday, ...
     *             6 for saturday
     *             MUST HAVE LENGTH OF EXACTLY GlobalConfig.WEEK_LENGTH
     * @param start the time of day this availability begins at
     * @param end the time of day this availability ends at
     * @param week the week_of_month this availability occurs on; 0 for every week
     */
    public Availability(boolean[] days, TimeOfDay start, TimeOfDay end, int week){
        if (week < 0 || week > 5){
            throw new IllegalArgumentException("Week must be a valid week_of_month or 0 for all weeks.");
        }
        if (days.length != GlobalConfig.WEEK_LENGTH){
            throw new IllegalArgumentException("Days array must have length " + GlobalConfig.WEEK_LENGTH + "; one index for each day.");
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
        for (int i = FIRST_DAY_OF_WEEK; i <= LAST_DAY_OF_WEEK; i++)
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

    /**
     * Checks if the given Gregorian Calendar date and time fall within this availability
     * @param c the date and time to check
     * @return boolean indicating if c is within the availability
     */
    public boolean duringThis(GregorianCalendar c)
    {
        int cWeekOfMonth = c.get(Calendar.WEEK_OF_MONTH);
        int cDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int cHour = c.get(Calendar.HOUR_OF_DAY);
        int cMinute = c.get(Calendar.MINUTE);
        TimeOfDay cTimeOfDay = new TimeOfDay(cHour, cMinute);
        if ((cWeekOfMonth == week || week == 0) && days[cDayOfWeek-1] == true && cTimeOfDay.afterOrEqual(start)
                && cTimeOfDay.beforeOrEqual(end))
        {
            return true;
        }
        return false;
    }

    public int getId(){return id;}

    public void setId(int id){this.id = id; }
}
