package Models.Provider;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by jmgoo on 1/11/2017.
 */
public interface Recurrence {
    /**
     * Constructs a recurrence from a JSON string
     * @param json the JSON string
     * @return the recurrence
     */
    static Recurrence fromJSONString(String json) throws ParseException, NoSuchMethodException{
        try {
            JSONParser parser = new JSONParser();
            JSONObject parsed = (JSONObject) parser.parse(json);
            String cn = (String) parsed.get("class");
            Class<?> c = Class.forName(cn);
            Constructor<?> ctor = c.getConstructor();
            Object obj = ctor.newInstance();
            if (obj instanceof Recurrence){
                return ((Recurrence) obj).getImplementation(parsed);
            }
            else throw new IllegalArgumentException("Given JSON object is not an instance of Recurrence");
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException ex){
            throw new IllegalArgumentException("Given JSON String does not contain a valid implementation" +
                    " of the Recurrence interface.");
        }
    }


    public Recurrence getImplementation(JSONObject jsonObject);

    /**
     * Constructs a JSON string representing the Recurrence; JSON string must include fully qualified
     * class name labeled "class"
     * @return the JSON string
     */
    String toJSONString();
}
