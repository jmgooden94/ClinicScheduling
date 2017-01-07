package Models.Provider;

/**
 * Recurrence for an availability that occurs on a given week(s) of each month
 */
public class WeekOfMonthRecurrence implements Recurrence {
    /**
     * Array of weeks the availability recurs on
     */
    int[] weeks;

    /**
     * Constructs a new WeekOfMonthRecurrence
     * @param weeks Array of weeks of the month the availability recurs on
     */
    public WeekOfMonthRecurrence(int[] weeks){
        if (weeks.length > 5){
            throw new IllegalArgumentException("weeks array cannot be longer than 5");
        }
        this.weeks = weeks;
    }
}
