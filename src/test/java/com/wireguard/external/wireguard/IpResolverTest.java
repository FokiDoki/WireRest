package com.wireguard.external.wireguard;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class IpResolverTest {
        private static final Subnet SUBNET = Subnet.fromString("0.0.128.0/16");
        @Test
        void testResolve() {
            IpResolver ipResolver = new IpResolver(SUBNET);
            ipResolver.takeSubnet(Subnet.fromString("0.0.128.1/32"));
           // assertEquals(32767, ipResolver.());
        }

        @Test
        //fill IpResolver with 100000 real ips and check speed of resolve
        void testResolve2() {
            IpResolver ipResolver = new IpResolver(Subnet.fromString("0.0.0.0/8"));
            Instant start = Instant.now();
            for (int i = 0; i < 13; i += 7) {
                System.out.println(i);
                for (int j = 0; j < 250; j += 2) {
                    for (int k = 0; k < 250; k += 2) {
                        ipResolver.takeSubnet(Subnet.fromString("0." + j + "." + k + "." + i + "/32"));
                    }
                }
            }
            Instant end = Instant.now();
            System.out.println("Time: " + (end.toEpochMilli() - start.toEpochMilli()));
            System.out.println("Size: " + ipResolver.freeRanges.size());
            findFirstGreaterSpeedTest(0         , ipResolver);
            findFirstGreaterSpeedTest(2221      , ipResolver);
            findFirstGreaterSpeedTest(23333     , ipResolver);
            findFirstGreaterSpeedTest(333333    , ipResolver);
            findFirstGreaterSpeedTest(4124124   , ipResolver);

            findFirstGreaterSpeedTest(16316424   , ipResolver);
            findFirstGreaterSpeedTest(123123123 , ipResolver);
            findFirstGreaterSpeedTest(1231231231, ipResolver);
            findFirstGreaterSpeedTest(1231231232, ipResolver);


        }

        private long findFirstGreaterSpeedTest(long ip, IpResolver ipResolver) {
            Instant start = Instant.now();
            int firstAddress = ipResolver.findFirstGreater(ip);
            Instant end = Instant.now();
            Object o = null;
            if (firstAddress!=-1){
                o  =ipResolver.freeRanges.get(firstAddress);
                if (firstAddress!=0)
                    System.out.println("prev "+ipResolver.freeRanges.get(firstAddress-1));
            }
            System.out.println("Find first Greater "+ ip +", found"+firstAddress+"" +
                    "" + o +
                    " : " + (end.toEpochMilli() - start.toEpochMilli()));
            return firstAddress;
        }
}