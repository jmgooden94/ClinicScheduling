package Models.Provider;

import java.util.Arrays;

/**
 * Recurrence for an availability that occurs on a given week(s) of each month
 */
public class WeekOfMonthRecurrence implements Recurrence {
    /**
     * Array of weeks the availability recurs on
     */
    boolean[] weeks;

    /**
     * Constructs a new WeekOfMonthRecurrence
     * @param weeks Array of length 5 for weeks of month; a given index should be true if the availability recurs on that week
     */
    public WeekOfMonthRecurrence(boolean[] weeks){
        if (weeks.length != 5){
            throw new IllegalArgumentException("weeks array must be 5 long");
        }
        this.weeks = weeks;
    }

    /**
     * Constructs a new WeekOfMonthRecurrence where the given week is true; use 0 for all weeks
     * @param recurWeek The week of the month this recurrence should occur on
     */
    public WeekOfMonthRecurrence(int recurWeek){
        this.weeks = new boolean[5];
        if (recurWeek < 0 || recurWeek > 5){
            throw new IllegalArgumentException("Invalid week given.");
        }
        else if (recurWeek == 0){
            Arrays.fill(weeks, true);
        }
        else {
            Arrays.fill(weeks, false);
            weeks[recurWeek] = true;
        }
    }
}
