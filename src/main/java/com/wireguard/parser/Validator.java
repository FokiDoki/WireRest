package com.wireguard.parser;

public class Validator {

    public static String validateKey(String key){
        Utils.throwIfNotBase64(key);
        return key;
    }

    public static int validatePort(String port){
        int portInt;
        portInt = Integer.parseInt(port);
        if (portInt < 1 || portInt > 65535){
            throw new IllegalArgumentException("WgInterfaceParser.validateListenPort: port value out of range");
        }
        return portInt;
    }





}
