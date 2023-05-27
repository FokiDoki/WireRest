package com.wireguard.external.wireguard;

public class NoFreeIpException extends RuntimeException{
    public NoFreeIpException(String message){
        super(message);
    }
}
