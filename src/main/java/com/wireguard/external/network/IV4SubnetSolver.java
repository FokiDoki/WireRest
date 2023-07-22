package com.wireguard.external.network;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface IV4SubnetSolver extends ISubnetSolver {

    Subnet obtainFree(int mask);

    void obtain(Subnet subnet) throws AlreadyUsedException;

    void obtainIp(String ip);

    void release(Subnet subnet);

    boolean isUsed(Subnet subnet);

    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    class IpRange {
        private long least;
        private long biggest;

        public long getIpsCount() {
            return biggest - least + 1;
        }

        private String toIpString(long ip) {
            return String.format("%d.%d.%d.%d",
                    (ip >> 24) & 0xff,
                    (ip >> 16) & 0xff,
                    (ip >> 8) & 0xff,
                    ip & 0xff);
        }

        public String getLeastString() {
            return toIpString(least);
        }

        public String getBiggestString() {
            return toIpString(biggest);
        }
    }

}
