package com.rjboyce.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String VOLUNTEER = "ROLE_VOLUNTEER";

    public static final String ORGANIZER = "ROLE_ORGANIZER";

    public static final String ATTENDEE = "ROLE_ATTENDEE";

    private AuthoritiesConstants() {}
}
