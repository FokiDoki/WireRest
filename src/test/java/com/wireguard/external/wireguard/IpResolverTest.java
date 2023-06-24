package com.wireguard.external.wireguard;

import com.wireguard.external.network.IpResolver;
import com.wireguard.external.network.NoFreeIpException;
import com.wireguard.external.network.Subnet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IpResolverTest {
    private static final Subnet SUBNET = Subnet.valueOf("0.0.0.0/16");
    IpResolver ipResolver;
    @BeforeEach
    void setUp() {
        ipResolver = new IpResolver(SUBNET);
    }

    @Test
    void testTake() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.128.1/32"));
        assertEquals(65535L, ipResolver.getAvailableIpsCount(), ".getAvailableIpCount()");
        assertEquals(1L, ipResolver.getTakenIpsCount(), ".getTakenIpCount()");
        assertEquals(32768L, ipResolver.getAvailableRanges().get(0).getBiggest(), ".get(0).getBiggest()");
        assertEquals(0L, ipResolver.getAvailableRanges().get(0).getLeast(), ".get(0).getSmallest()");
    }

    @Test
    void testTakeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> ipResolver.takeSubnet(Subnet.valueOf("0.1.0.0/32")));
    }

    @Test
    void testTakeTaken() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.1/32"));
        assertThrows(NoFreeIpException.class, () -> ipResolver.takeSubnet(Subnet.valueOf("0.0.0.1/32")));
    }

    @Test
    void testTakeTakenTouching() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.1/28"));
        assertThrows(NoFreeIpException.class, () -> ipResolver.takeSubnet(Subnet.valueOf("0.0.0.15/29")));
    }

    @Test
    void testCountWhenNoFreeIp() {
        IpResolver ipResolver = new IpResolver(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        assertEquals(0L, ipResolver.getAvailableIpsCount(), ".getAvailableIpCount()");
        assertEquals(1L, ipResolver.getTakenIpsCount(), ".getTakenIpCount()");
    }

    @Test
    void testTakeWhenNoFreeIp() {
        IpResolver ipResolver = new IpResolver(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        assertThrows(NoFreeIpException.class, () -> ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32")));
        assertThrows(IllegalArgumentException.class, () -> ipResolver.takeSubnet(Subnet.valueOf("0.1.0.0/32")));
    }

    @Test
    void testTakeWith17Mask(){
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/17"));
        assertEquals(32768L, ipResolver.getTakenIpsCount(), ".getTakenIpCount()");
        assertEquals(32768L, ipResolver.getAvailableRanges().get(0).getLeast());
    }

    @Test
    void testFree() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.1/27"));
        ipResolver.freeSubnet(Subnet.valueOf("0.0.0.1/27"));
        assertEquals(65536L, ipResolver.getAvailableIpsCount(), ".getAvailableIpCount()");
        assertEquals(65535L, ipResolver.getAvailableRanges().get(0).getBiggest(), ".get(0).getBiggest()");
        assertEquals(0L, ipResolver.getAvailableRanges().get(0).getLeast(), ".get(0).getSmallest()");
    }

    @Test
    void testFreeWhenNoIpTaken() {
        assertThrows(IllegalArgumentException.class, () -> ipResolver.freeSubnet(Subnet.valueOf("")));
    }

    @Test
    void testFreeWhenNoIpLeft(){
        IpResolver ipResolver = new IpResolver(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.freeSubnet(Subnet.valueOf("0.0.0.0/32"));
        assertEquals(1L, ipResolver.getAvailableIpsCount(), ".getAvailableIpCount()");
    }

    @Test
    void testFreeWhenNoIpLeftAndOutOfRange(){
        IpResolver ipResolver = new IpResolver(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        assertThrows(IllegalArgumentException.class, () ->  ipResolver.takeSubnet(Subnet.valueOf("0.0.0.1/32")));
    }



    @Test
    void testTakeFreeSubnet(){
        Subnet subnet = ipResolver.takeFreeSubnet(32);
        assertEquals(1L, ipResolver.getTakenIpsCount(), ".getTakenIpCount()");
        assertEquals(Subnet.valueOf("0.0.0.0/32"), subnet);
    }

    @Test
    void testTakeFreeWhenFirstAndThirdAlreadyTaken() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.2/32"));
        Subnet subnet = ipResolver.takeFreeSubnet(32);
        assertEquals(3L, ipResolver.getTakenIpsCount(), ".getTakenIpCount()");
        assertEquals(Subnet.valueOf("0.0.0.1/32"), subnet);
    }

    @Test
    void testTakeFreeWhenMaskIs31AndFirstAndThirdAlreadyTaken() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.2/32"));
        Subnet subnet = ipResolver.takeFreeSubnet(31);
        assertEquals(4L, ipResolver.getTakenIpsCount(), ".getTakenIpCount()");
        assertEquals(Subnet.valueOf("0.0.0.4/31"), subnet);
    }

    @Test
    void testTakeFreeWhenNoPlaceOnFirst(){
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.63/32"));
        Subnet subnet = ipResolver.takeFreeSubnet(26);
        assertEquals(Subnet.valueOf("0.0.0.64/26"), subnet);
    }

    @Test
    void testTakeFreeWhenNoPlaceOnFirstAndThird() {
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.63/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.64/32"));
        Subnet subnet = ipResolver.takeFreeSubnet(26);
        assertEquals(Subnet.valueOf("0.0.0.128/26"), subnet);
    }

    @Test
    void testTakeFreeWhenNoIpLeft(){
        ipResolver = new IpResolver(Subnet.valueOf("0.0.0.0/32"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.0/32"));
        assertThrows(NoFreeIpException.class, () -> ipResolver.takeFreeSubnet(32));
    }

    @Test
    void testTakeFreeWhenNoIpWithMaskLeft() {
        ipResolver = new IpResolver(Subnet.valueOf("0.0.0.0/29"));
        ipResolver.takeSubnet(Subnet.valueOf("0.0.0.4/32"));
        assertThrows(NoFreeIpException.class, () -> ipResolver.takeFreeSubnet(29));
    }

}