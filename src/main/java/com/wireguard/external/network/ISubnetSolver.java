package com.wireguard.external.network;

public interface ISubnetSolver {

    void obtainIp(String ip);

    long getAvailableIpsCount();

    long getTotalIpsCount();

    long getUsedIpsCount();
}
