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
     * if this patient smokes
     */
    private boolean smoker;

    /**
     * Constructor for patient class
     * @param firstName first name of patient
     * @param lastName last name of patient
     * @param phone phone number of patient (stored as string)
     * @param address patient's physical address
     * @param smoker if this patient smokes
     */
    public Patient(String firstName, String lastName, String phone, Address address, boolean smoker) {
        if (firstName == null || lastName == null || phone == null || address == null){
            throw new IllegalArgumentException("firstName, lastName, phone, and address cannot be null");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.smoker = smoker;
    }
}
