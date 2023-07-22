package com.wireguard.external.network;

import com.googlecode.ipv6.IPv6Network;

import java.math.BigInteger;

public class SubnetV6 implements ISubnet, Comparable<SubnetV6>{

    private final IPv6Network address;

    public SubnetV6(IPv6Network address) {
        this.address = address;
    }

    public static SubnetV6 valueOf(String subnet) {
        return new SubnetV6(IPv6Network.fromString(subnet));
    }

    @Override
    public BigInteger getIpCount() {
        return address.getFirst().toBigInteger().subtract(address.getLast().toBigInteger());
    }

    @Override
    public byte[] getFirstIpBytes() {
        return address.getFirst().toByteArray();
    }

    @Override
    public byte[] getLastIpBytes() {
        return address.getLast().toByteArray();
    }

    public BigInteger getLastIpNumeric() {
        return address.getLast().toBigInteger();
    }

    public BigInteger getFirstIpNumeric() {
        return address.getFirst().toBigInteger();
    }


    @Override
    public String getIpString() {
        return address.toString();
    }

    @Override
    public String getFirstIpString() {
        return address.getFirst().toString();
    }

    @Override
    public String getLastIpString() {
        return address.getLast().toString();
    }

    @Override
    public int compareTo(SubnetV6 o) {
        return getFirstIpNumeric().compareTo(o.getFirstIpNumeric());
    }

    @Override
    public String toString() {
        return getIpString();
    }
}
