package Models.Provider;

import Models.TimeOfDay;
import UI.Panels.MultiColumnView;

import java.awt.*;
import java.sql.Time;
import java.util.List;

/**
 * Stores information about a provider
 */
public class Provider {
    /**
     * The type of provider.
     */
     private ProviderType providerType;
    /**
     * The provider's first name
     */
    private String firstName;
    /**
     * The provider's last name
     */
    private String lastName;
    /**
     * The provider's availability
     */
    private List<Availability> availability;
    /**
     * The provider's id from the db; should be set by getProviders and addProvider
     */
    private int id;

    /**
     * The color the cells corresponding to this provider
     * are to be rendered.
     */
    private Color color = MultiColumnView.DEFAULT_COLOR;

    /**
     * The starting TimeOfDay for a day when this provider is available
     * this field really just gets populated when a provider is retrieved from the database.
     * Otherwise it is useless, as it contains no data that is not already contained in availability.
     * It's kind of a hack to make sorting easier.
     */
    private TimeOfDay start;

    /**
     * The ending TimeOfDay for a day when this provider is available
     * this field really just gets populated when a provider is retrieved from the database.
     * Otherwise it is useless, as it contains no data that is not already contained in availability.
     * It's kind of a hack to make sorting easier.
     */
    private TimeOfDay end;

    public TimeOfDay getStart() { return start; }

    public TimeOfDay getEnd() { return end; }

    public void setStart(TimeOfDay t) {this.start = t;}

    public void setEnd(TimeOfDay t) {this.end = t;}

    public boolean during(TimeOfDay t)
    {
        return this.start.beforeOrEqual(t) && this.end.after(t);
    }


    /**
     * Constructs a new provider
     * @param providerType The type of provider.
     * @param firstName The provider's first name
     * @param lastName The provider's last name
     * @param availability A list providing the provider's availability
     */
    public Provider(ProviderType providerType, String firstName, String lastName, List<Availability> availability) {
        this.providerType = providerType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.availability = availability;
    }

    public ProviderType getProviderType(){
        return providerType;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getName(){
        return firstName + " " + lastName;
    }

    public Color getColor() { return this.color; }

    public void setColor(Color c) { this.color = c; }

    public List<Availability> getAvailability(){
        return availability;
    }

    public void setId(int id){ this.id = id; }

    public int getId() { return id; }

    @Override
    public String toString()
    {
        return firstName + " " + lastName + ", " + providerType.getAbbreviation();
    }
}
