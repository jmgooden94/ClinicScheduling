package Models.Patient;

/**
 * Class to store clinic patients
 */
public class Patient {
    /**
     * patient's first name
     */
    private String firstName;
    /**
     * patient's last name
     */
    private String lastName;
    /**
     * patient's phone number (stored as a string)
     */
    private String phone;
    /**
     * Patient's address
     */
    private Address address;

    /**
     * Constructor for patient class
     * @param firstName first name of patient
     * @param lastName last name of patient
     * @param phone phone number of patient (stored as string)
     * @param address patient's physical address
     */
    public Patient(String firstName, String lastName, String phone, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }

    @Override
    public String toString()
    {
        return firstName + " " + lastName;
    }
}
