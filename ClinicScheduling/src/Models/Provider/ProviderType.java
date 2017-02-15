package Models.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumerator for types of providers in the clinic
 */
public enum ProviderType {
    PHYSICIAN ("Physician", "Dr."),
    NURSE_PRACTICIONER ("Nurse Practicioner", "N.P."),
    LAB ("Lab", "Lab"),
    PHYSICAL_THERAPIST ("Physical Therapist", "P.T."),
    INTAKE ("Intake", "In"),
    CHIROPRACTOR ("Chiropractor", "Chiro."),
    COUNSELOR ("Counselor", "Couns."),
    PHYSICIAN_ASSISTANT ("Physician Assistant", "P.A."),
    INTERPRETER ("Interpreter", "Interp.");

    /**
     * The name of the provider type
     */
    private String name;
    /**
     * The abbreviation for the provider type
     */
    private String abbreviation;

    /**
     * Constructor for ProviderType enum
     * @param name the name of the provider type
     * @param abbr the abbreviation for the provider type
     */
    ProviderType(String name, String abbr){
        this.name = name;
        this.abbreviation = abbr;
    }

    /**
     * Gets the provider type's name
     * @return the provider type's name
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the provider type's abbreviation
     * @return the provider type's abbreviation
     */
    public String getAbbreviation(){
        return abbreviation;
    }

    /**
     * Gets the plain text names of all provider types
     * @return a list containing the plain text names of all provider types
     */
    public static List<String> getNames(){
        List<String> names = new ArrayList<>();
        for (ProviderType pt : values()){
            names.add(pt.name);
        }
        return names;
    }

    /**
     * Gets the ProviderType value from its plain text name
     * @param name the name to get the value of
     * @return
     */
    public static ProviderType fromName(String name){
        String key = name.toUpperCase();
        key = key.replace(' ', '_');
        return valueOf(key);
    }
}
