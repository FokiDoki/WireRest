package com.wireguard.external.network;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface ISubnetSolver {

    Subnet obtainFree(int mask);

    void obtain(Subnet subnet) throws AlreadyUsedException;

    void obtainIp(String ip);

    void release(Subnet subnet);

    boolean isUsed(Subnet subnet);

    long getAvailableIpsCount();

    long getTotalIpsCount();

    long getUsedIpsCount();

    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    class IpRange {
        private long least;
        private long biggest;
        public long getIpsCount(){
            return biggest - least + 1;
        }
    }

}
