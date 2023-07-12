package com.wireguard.external.network;

import java.util.List;

public interface ISubnet extends Comparable<ISubnet> {


    long getIpCount();
    byte[] getFirstIpBytes();
    byte[] getLastIpBytes();
    long getLastIpNumeric();
    long getFirstIpNumeric();
    List<Integer> getIp();
    String getIpString();
    String getFirstIpString();
    String getLastIpString();
    List<Integer> getFirstIp();
    List<Integer> getLastIp();


}
