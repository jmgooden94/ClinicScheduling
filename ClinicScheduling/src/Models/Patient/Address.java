package Models.Patient;

/**
 * Class for storing a patient's physical address
 */
public class Address {
    private String street;
    private String city;
    private Models.State state;
    private String zip;

    public Address(String street, String city, Models.State state, String zip){
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet(){
        return street;
    }

    public String getCity(){
        return city;
    }

    public Models.State getState(){
        return state;
    }

    public String getZip(){
        return zip;
    }
}
