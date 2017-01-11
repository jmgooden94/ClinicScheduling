package Models;

import java.util.HashMap;

/**
 * Enumerator for US states
 */
public enum State {
    ALABAMA("Alabama", "AL"), ALASKA("Alaska", "AK"), AMERICAN_SAMOA("American Samoa", "AS"), ARIZONA("Arizona", "AZ"), ARKANSAS(
    "Arkansas", "AR"), CALIFORNIA("California", "CA"), COLORADO("Colorado", "CO"), CONNECTICUT("Connecticut", "CT"), DELAWARE(
    "Delaware", "DE"), DISTRICT_OF_COLUMBIA("District of Columbia", "DC"), FEDERATED_STATES_OF_MICRONESIA(
    "Federated States of Micronesia", "FM"), FLORIDA("Florida", "FL"), GEORGIA("Georgia", "GA"), GUAM("Guam", "GU"), HAWAII(
    "Hawaii", "HI"), IDAHO("Idaho", "ID"), ILLINOIS("Illinois", "IL"), INDIANA("Indiana", "IN"), IOWA("Iowa", "IA"), KANSAS(
    "Kansas", "KS"), KENTUCKY("Kentucky", "KY"), LOUISIANA("Louisiana", "LA"), MAINE("Maine", "ME"), MARYLAND("Maryland", "MD"), MARSHALL_ISLANDS(
    "Marshall Islands", "MH"), MASSACHUSETTS("Massachusetts", "MA"), MICHIGAN("Michigan", "MI"), MINNESOTA("Minnesota", "MN"), MISSISSIPPI(
    "Mississippi", "MS"), MISSOURI("Missouri", "MO"), MONTANA("Montana", "MT"), NEBRASKA("Nebraska", "NE"), NEVADA("Nevada",
    "NV"), NEW_HAMPSHIRE("New Hampshire", "NH"), NEW_JERSEY("New Jersey", "NJ"), NEW_MEXICO("New Mexico", "NM"), NEW_YORK(
    "New York", "NY"), NORTH_CAROLINA("North Carolina", "NC"), NORTH_DAKOTA("North Dakota", "ND"), NORTHERN_MARIANA_ISLANDS(
    "Northern Mariana Islands", "MP"), OHIO("Ohio", "OH"), OKLAHOMA("Oklahoma", "OK"), OREGON("Oregon", "OR"), PALAU("Palau",
    "PW"), PENNSYLVANIA("Pennsylvania", "PA"), PUERTO_RICO("Puerto Rico", "PR"), RHODE_ISLAND("Rhode Island", "RI"), SOUTH_CAROLINA(
    "South Carolina", "SC"), SOUTH_DAKOTA("South Dakota", "SD"), TENNESSEE("Tennessee", "TN"), TEXAS("Texas", "TX"), UTAH(
    "Utah", "UT"), VERMONT("Vermont", "VT"), VIRGIN_ISLANDS("Virgin Islands", "VI"), VIRGINIA("Virginia", "VA"), WASHINGTON(
    "Washington", "WA"), WEST_VIRGINIA("West Virginia", "WV"), WISCONSIN("Wisconsin", "WI"), WYOMING("Wyoming", "WY");

    /**
     * Constructor for State enumerator
     * @param name the name of the state
     * @param abbr the abbreviation for the state
     */
    State(String name, String abbr) {
        this.name = name;
        this.abbreviation = abbr;
    }

    /**
     * The name of the state
     */
    private String name;
    /**
     * The abbreviation for the state
     */
    private String abbreviation;

    /**
     * Gets all of the names of the states
     * @return String array containing the names of the states
     */
    public static String[] getNames(){
        String[] names = new String[values().length];
        for(int i = 0; i < values().length; i++){
            names[i] = values()[i].name;
        }
        return names;
    }

    /**
     * Gets all of the abbreviations of the states
     * @return String array containing the abbreviations of the states
     */
    public static String[] getAbbreviations(){
        String[] abbrs = new String[values().length];
        for(int i = 0; i < values().length; i++){
            abbrs[i] = values()[i].name;
        }
        return abbrs;
    }

    /**
     * Gets the number of states
     * @return the number of states
     */
    public static int count(){
        return values().length;
    }

    public static State fromName(String name){
        String key = name.toUpperCase();
        key = key.replace(' ', '_');
        return valueOf(key);
    }
}
