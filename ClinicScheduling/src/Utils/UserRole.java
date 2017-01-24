package Utils;

/**
 * enum for application user roles
 */
public enum UserRole {
    USER,
    ADMIN;
    // Note that the values of this enum are used in MySQL without being sanitized; for this reason, their length
    // MUST NOT EXCEED 30 CHARACTERS
}
