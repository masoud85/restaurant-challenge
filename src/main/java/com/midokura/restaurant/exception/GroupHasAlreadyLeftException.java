package com.midokura.restaurant.exception;

public class GroupHasAlreadyLeftException extends RuntimeException {
    public GroupHasAlreadyLeftException() {
        super("Either the group has already gone or it does not exist at all.");
    }
}
