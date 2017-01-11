package Models;

/**
 * Simple class representing the type of day in hours and minutes
 */
public class TimeOfDay {
    /**
     * The hour of the day, from 0 to 12
     */
    private int hour;
    /**
     * The minute of the hour, from 0 to 59
     */
    private int minute;
    /**
     * True if this time is pm
     */
    private boolean pm;
    /**
     * Constructs a new TimeOfDay
     * @param hour the hour of the day, from 0 to 12
     * @param minute the minute of the hour, from 0 to 59
     * @param pm true if this time is pm
     */
    public TimeOfDay(int hour, int minute, boolean pm){
        if (hour < 0 || hour > 12 || minute < 0 || minute > 59){
            throw new IllegalArgumentException("Hour must be between 0 and 12, minute must be between 0 and 59");
        }
        this.hour = hour;
        this.minute = minute;
        this.pm = pm;
    }

    /**
     * Gets the hour of the day
     * @return the hour of the day
     */
    public int getHour(){
        return hour;
    }

    /**
     * Gets the minute of the hour
     * @return the minute of the hour
     */
    public int getMinute(){
        return minute;
    }

    /**
     * Gets whether the time is AM/PM
     * @return true for pm, false for am
     */
    public boolean getPM(){
        return pm;
    }

    @Override
    public String toString(){
        String time = String.format("%1$01d:%2$02d", hour, minute);
        if (pm) return time + "PM";
        else return time + "AM";
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof TimeOfDay))
        {
            return false;
        }
        TimeOfDay oth = (TimeOfDay) other;

        return (
                    oth.getHour() == hour
                    && oth.getMinute() == minute
                    && oth.getPM() == pm
                );
    }
}
