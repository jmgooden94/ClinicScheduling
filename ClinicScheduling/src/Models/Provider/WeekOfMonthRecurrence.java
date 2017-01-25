package Models.Provider;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringJoiner;

/**
 * Recurrence for an availability that occurs on a given week(s) of each month
 */
public class WeekOfMonthRecurrence implements Recurrence {
    /**
     * Array of weeks the availability recurs on
     */
    boolean[] weeks;

    /**
     * No-op constructor for use with .fromJSONString
     */
    public WeekOfMonthRecurrence(){

    }

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

    /**
     * Constructs a recurrence from a JSON object
     * @param jsonObject the JSON object
     * @return the recurrence
     */
    @Override
    public Recurrence getImplementation(JSONObject jsonObject) {
        this.weeks = new boolean[5];
        JSONArray weeksFromJSON = (JSONArray) jsonObject.get("weeks");
        Iterator<Boolean> iterator = weeksFromJSON.iterator();
        int i = 0;
        while (iterator.hasNext()){
            this.weeks[i] = iterator.next();
            i++;
        }
        return this;
    }

    @Override
    public String toJSONString(){
        JSONObject obj = new JSONObject();
        obj.put("class", this.getClass().getName());
        JSONArray arr = new JSONArray();
        for (int i = 0; i < weeks.length; i++){
            arr.add(i, weeks[i]);
        }
        obj.put("weeks", arr);
        return obj.toJSONString();
    }
}