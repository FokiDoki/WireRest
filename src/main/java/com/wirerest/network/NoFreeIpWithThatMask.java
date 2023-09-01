package com.wirerest.network;

public class NoFreeIpWithThatMask extends NoFreeIpException{
    public NoFreeIpWithThatMask(int mask) {
        super("No free IP addresses available with mask %d".formatted(mask));
    }
}
