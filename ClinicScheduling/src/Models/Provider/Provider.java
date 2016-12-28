package Models.Provider;

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

    public List<Availability> getAvailability(){
        return availability;
    }
}
