package com.wireguard.external.wireguard;

public class NoFreeIpException extends RuntimeException{
    public NoFreeIpException(){
        super("No free ip left");
    }
}
