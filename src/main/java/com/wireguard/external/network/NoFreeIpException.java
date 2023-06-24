package com.wireguard.external.network;

public class NoFreeIpException extends RuntimeException{
    public NoFreeIpException(String message){
        super(message);
    }
}
