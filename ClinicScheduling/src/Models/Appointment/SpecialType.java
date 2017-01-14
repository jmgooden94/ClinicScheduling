package Models.Appointment;

import Models.State;

/**
 * Enum for special types of appointments
 */
public enum SpecialType {
    ASTHMA ("Asthma"),
    DIETITIAN ("Dietitian"),
    PHYSICAL_THERAPY ("Physical Therapy"),
    COUNSELING ("Counseling"),
    INTAKE ("Intake"),
    CHIROPRACTOR ("Chiropractor"),
    HIGH_BLOOD_PRESSURE ("High Blood Pressure"),
    DIABETES ("Diabetes");

    private String name;

    SpecialType(String name){
        this.name = name;
    }

    /**
     * Gets all of the names of the special appt types
     * @return String array containing the names of the types
     */
    public static String[] getNames(){
        String[] names = new String[values().length];
        for(int i = 0; i < values().length; i++){
            names[i] = values()[i].name;
        }
        return names;
    }

    /**
     * Returns the enum value of the SpecialType from the given string
     * @param name the plain text name of the special type
     * @return the SpecialType enum value
     */
    public static SpecialType fromName(String name){
        String key = name.toUpperCase();
        key = key.replace(' ', '_');
        return valueOf(key);
    }
}
