package com.wirerest.network;

import java.math.BigInteger;

public interface ISubnet {


    BigInteger getIpCount();

    byte[] getFirstIpBytes();

    byte[] getLastIpBytes();

    String getIpString();

    String getFirstIpString();

    String getLastIpString();


}
