package Models.Provider;

/**
 * Enumerator for types of providers in the clinic
 */
public enum ProviderType {
    PHYSICIAN ("Physician", "Dr."),
    NURSE_PRACTICIONER ("Nurse Practicioner", "N.P."),
    LAB ("Lab", "Lab"),
    PHYSICAL_THERAPIST ("Physical Therapist", "P.T."),
    INTAKE ("Intake", "In");

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
}
