package Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmgoo on 12/28/2016.
 */
public enum Day {
    SUNDAY ("Sunday", "Sun.", 'U', 0),
    MONDAY ("Monday", "Mon.", 'M', 1),
    TUESDAY ("Tuesday", "Tue.", 'T', 2),
    WEDNESDAY ("Wednesday", "Wed.", 'W', 3),
    THURSDAY ("Thursday", "Thu.", 'R', 4),
    FRIDAY ("Friday", "Fri.", 'F', 5),
    SATURDAY ("Saturday", "Sat.", 'S', 6);

    /**
     * The day's name
     */
    private String name;
    /**
     * The day's abbreviation
     */
    private String abbr;
    /**
     * The day's single character abbreviation
     */
    private char charAbbrev;
    /**
     * The day's position in the week
     */
    private int dayOfWeek;
    /**
     * Constructs a new Day enumeration
     * @param name The day's name
     * @param abbr The day's abbreviation
     * @param dayOfWeek The day's position in the week
     */
    Day(String name, String abbr, char charAbbrev, int dayOfWeek){
        this.name = name;
        this.abbr = abbr;
        this.charAbbrev = charAbbrev;
        this.dayOfWeek = dayOfWeek;
    }

    public String getAbbr(){
        return abbr;
    }

    public char getCharAbbrev(){
        return charAbbrev;
    }

    public int getDayOfWeek(){
        return dayOfWeek;
    }

    /**
     * Returns the day of the week corresponding to the given column in the table.
     * The first column (0) is the header column; the second (1) is Monday, the
     * third (2) is Tuesday, and so on.
     * @param index the 0-based column to examine
     * @return the corresponding weekday, from MONDAY to FRIDAY
     */
    public static Day toDay(int index) {
        if (index < 0 || index > 6) {
            throw new IllegalArgumentException("index out of range: " + index);
        } else {
            return Day.values()[index];
        }
    }

    /**
     * Returns a list containing the names of all days
     * @return the list
     */
    public static List<String> getDays(){
        List<String> days = new ArrayList<>();
        for (Day d : Day.values()){
            days.add(d.name);
        }
        return days;
    }

    /**
     * Returns a list containing the names of all weekdays
     * @return the list
     */
    public static List<String> getWeekdays(){
        List<String> days = new ArrayList<>();
        for (int i = 1; i < 6; i++){
            days.add(Day.values()[i].name);
        }
        return days;
    }
}