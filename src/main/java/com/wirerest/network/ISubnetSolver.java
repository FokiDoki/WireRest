package com.wirerest.network;

public interface ISubnetSolver {

    void obtainIp(String ip);

    long getAvailableIpsCount();

    long getTotalIpsCount();

    long getUsedIpsCount();
}
