package Models.Provider;

import Models.TimeOfDay;
import UI.Panels.MultiColumnView;

import java.awt.*;
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

    // TODO: remove, it's a hack
    public TimeOfDay start;

    // TODO: remove, it's a hack
    public TimeOfDay end;

    // TODO: remove, it's a hack
    public TimeOfDay getStart() { return start; }

    // TODO: remove, it's a hack
    public TimeOfDay getEnd() { return end; }

    // TODO: remove, it's a hack
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
