package Models.Patient;

import Models.State;

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
     * patient's street address
     */
    private String street;
    /**
     * patient's city of residence
     */
    private String city;
    /**
     * patient's state of residence
     */
    private State state;
    /**
     * patient's ZIP code
     */
    private int zip;

    /**
     * Constructor for patient class
     * @param firstName first name of patient
     * @param lastName last name of patient
     * @param phone phone number of patient (stored as string)
     * @param street street address of patient
     * @param city patient's city of residence
     * @param state patient's state of residence
     * @param zip patient's zip code
     */
    public Patient(String firstName, String lastName, String phone, String street, String city, State state, int zip) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }
}
