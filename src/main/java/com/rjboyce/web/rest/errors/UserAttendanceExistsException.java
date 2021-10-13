package com.rjboyce.web.rest.errors;

public class UserAttendanceExistsException extends RuntimeException {

    public UserAttendanceExistsException() {
        super("User Attendance Already Exists For That Event.");
    }
}
