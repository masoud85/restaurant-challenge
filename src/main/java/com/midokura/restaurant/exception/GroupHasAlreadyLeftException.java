package com.midokura.restaurant.exception;

public class GroupHasAlreadyLeftException extends RuntimeException {
    public GroupHasAlreadyLeftException() {
        super("Group has already gone.");
    }
}
