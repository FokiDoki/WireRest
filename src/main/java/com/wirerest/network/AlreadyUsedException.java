package com.wirerest.network;

public class AlreadyUsedException extends RuntimeException {
    public AlreadyUsedException(Subnet subnet) {
        super("Subnet %s is is already used".formatted(subnet.toString()));
    }
}
