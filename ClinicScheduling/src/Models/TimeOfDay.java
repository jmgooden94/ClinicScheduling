package Models;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
     * Constructs a new TimeOfDay
     * @param hour the hour of the day in 24h format, from 0 to 23
     * @param minute the minute of the hour, from 0 to 59
     */
    public TimeOfDay(int hour, int minute){
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59){
            throw new IllegalArgumentException("Hour must be between 1 and 12, minute must be between 0 and 59");
        }
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Gets the hour of the day in 24h format, from 0 to 23
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

    @Override
    public String toString(){
        return String.format("%1$01d:%2$02d", hour, minute);
    }

    /**
     * Returns a string representing the time in 12 hour format
     * @return a string representing the time in 12 hour format
     */
    public String to12String(){
        int h12 = hour;
        String ampm = "AM";
        if (hour > 12){
            ampm = "PM";
            h12 -= 12;
        }
        if (hour == 0){
            h12 = 12;
        }
        return (String.format("%1$01d:%2$02d", h12, minute) + " " + ampm);
    }

    /**
     * Converts the TimeOfDay into a sql time object for insertion into DB
     * @return the sql time
     */
    public Time toSqlTime(){
        GregorianCalendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        Date d = c.getTime();
        return new Time(d.getTime());
    }

    /**
     * Converts the given sql time object into a TimeOfDay
     * @param t the sql time
     * @return the TimeOfDay built from the sql time
     */
    public static TimeOfDay fromSqlTime(Time t){
        if (t == null){throw new IllegalArgumentException("Time t cannot be null");}
        Date d = new Date(t.getTime());
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);
        return new TimeOfDay(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    /**
     * Checks if this time is before the given time t
     * @param t the time to compare
     * @return true if this time is before given time t; else false
     */
    public boolean before(TimeOfDay t){
        if(this.hour < t.hour){
            return true;
        }
        else if (this.hour == t.hour){
            if(this.minute < t.minute){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this time is before or equal to the given time t
     * @param t the time to compare
     * @return true if this time is before given time t; else false
     */
    public boolean beforeOrEqual(TimeOfDay t){
        if(this.hour < t.hour){
            return true;
        }
        else if (this.hour == t.hour){
            if(this.minute <= t.minute){
                return true;
            }
        }
        return false;
    }
}
