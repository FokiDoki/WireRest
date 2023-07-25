package com.wireguard.external.wireguard;

import com.wireguard.external.network.AlreadyUsedException;
import com.wireguard.external.network.NoFreeIpException;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.network.SubnetSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubnetSolverTest {
    private static final Subnet SUBNET = Subnet.valueOf("0.0.0.0/16");
    SubnetSolver subnetSolver;

    @BeforeEach
    void setUp() {
        subnetSolver = new SubnetSolver(SUBNET);
    }

    @Test
    void testTake() {
        subnetSolver.obtain(Subnet.valueOf("0.0.128.1/32"));
        assertEquals(65535L, subnetSolver.getAvailableIpsCount(), ".getAvailableIpCount()");
        assertEquals(1L, subnetSolver.getUsedIpsCount(), ".getTakenIpCount()");
        assertEquals(32768L, subnetSolver.getAvailableRanges().get(0).getBiggest(), ".get(0).getBiggest()");
        assertEquals(0L, subnetSolver.getAvailableRanges().get(0).getLeast(), ".get(0).getSmallest()");
    }

    @Test
    void testTakeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> subnetSolver.obtain(Subnet.valueOf("0.1.0.0/32")));
    }

    @Test
    void testTakeTaken() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.1/32"));
        assertThrows(AlreadyUsedException.class, () -> subnetSolver.obtain(Subnet.valueOf("0.0.0.1/32")));
    }

    @Test
    void testTakeReleaseSideBySide() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.16/31"));
        subnetSolver.release(Subnet.valueOf("0.0.0.17/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.18/32"));
        subnetSolver.release(Subnet.valueOf("0.0.0.18/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.19/32"));
    }

    @Test
    void testTakeTakenTouching() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.1/28"));
        assertThrows(AlreadyUsedException.class, () -> subnetSolver.obtain(Subnet.valueOf("0.0.0.15/29")));
    }

    @Test
    void testCountWhenNoFreeIp() {
        SubnetSolver subnetSolver = new SubnetSolver(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        assertEquals(0L, subnetSolver.getAvailableIpsCount(), ".getAvailableIpCount()");
        assertEquals(1L, subnetSolver.getUsedIpsCount(), ".getTakenIpCount()");
    }

    @Test
    void testTakeWhenNoFreeIp() {
        SubnetSolver subnetSolver = new SubnetSolver(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        assertThrows(NoFreeIpException.class, () -> subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32")));
        assertThrows(IllegalArgumentException.class, () -> subnetSolver.obtain(Subnet.valueOf("0.1.0.0/32")));
    }

    @Test
    void testTakeWith17Mask() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/17"));
        assertEquals(32768L, subnetSolver.getUsedIpsCount(), ".getTakenIpCount()");
        assertEquals(32768L, subnetSolver.getAvailableRanges().get(0).getLeast());
    }

    @Test
    void testFree() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.1/27"));
        subnetSolver.release(Subnet.valueOf("0.0.0.1/27"));
        assertEquals(65536L, subnetSolver.getAvailableIpsCount(), ".getAvailableIpCount()");
        assertEquals(65535L, subnetSolver.getAvailableRanges().get(0).getBiggest(), ".get(0).getBiggest()");
        assertEquals(0L, subnetSolver.getAvailableRanges().get(0).getLeast(), ".get(0).getSmallest()");
    }

    @Test
    void testFreeWhenNoIpTaken() {
        assertThrows(IllegalArgumentException.class, () -> subnetSolver.release(Subnet.valueOf("")));
    }

    @Test
    void testFreeWhenNoIpLeft() {
        SubnetSolver subnetSolver = new SubnetSolver(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.release(Subnet.valueOf("0.0.0.0/32"));
        assertEquals(1L, subnetSolver.getAvailableIpsCount(), ".getAvailableIpCount()");
    }

    @Test
    void testFreeWhenNoIpLeftAndOutOfRange() {
        SubnetSolver subnetSolver = new SubnetSolver(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        assertThrows(IllegalArgumentException.class, () -> subnetSolver.obtain(Subnet.valueOf("0.0.0.1/32")));
    }


    @Test
    void testTakeFreeSubnet() {
        Subnet subnet = subnetSolver.obtainFree(32);
        assertEquals(1L, subnetSolver.getUsedIpsCount(), ".getTakenIpCount()");
        assertEquals(Subnet.valueOf("0.0.0.0/32"), subnet);
    }

    @Test
    void testTakeFreeWhenFirstAndThirdAlreadyTaken() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.2/32"));
        Subnet subnet = subnetSolver.obtainFree(32);
        assertEquals(3L, subnetSolver.getUsedIpsCount(), ".getTakenIpCount()");
        assertEquals(Subnet.valueOf("0.0.0.1/32"), subnet);
    }

    @Test
    void testTakeFreeWhenMaskIs31AndFirstAndThirdAlreadyTaken() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.2/32"));
        Subnet subnet = subnetSolver.obtainFree(31);
        assertEquals(4L, subnetSolver.getUsedIpsCount(), ".getTakenIpCount()");
        assertEquals(Subnet.valueOf("0.0.0.4/31"), subnet);
    }

    @Test
    void testTakeFreeWhenNoPlaceOnFirst() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.63/32"));
        Subnet subnet = subnetSolver.obtainFree(26);
        assertEquals(Subnet.valueOf("0.0.0.64/26"), subnet);
    }

    @Test
    void testTakeFreeWhenNoPlaceOnFirstAndThird() {
        subnetSolver.obtain(Subnet.valueOf("0.0.0.63/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.64/32"));
        Subnet subnet = subnetSolver.obtainFree(26);
        assertEquals(Subnet.valueOf("0.0.0.128/26"), subnet);
    }

    @Test
    void testTakeFreeWhenNoIpLeft() {
        subnetSolver = new SubnetSolver(Subnet.valueOf("0.0.0.0/32"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.0/32"));
        assertThrows(NoFreeIpException.class, () -> subnetSolver.obtainFree(32));
    }

    @Test
    void testTakeFreeWhenNoIpWithMaskLeft() {
        subnetSolver = new SubnetSolver(Subnet.valueOf("0.0.0.0/29"));
        subnetSolver.obtain(Subnet.valueOf("0.0.0.4/32"));
        assertThrows(NoFreeIpException.class, () -> subnetSolver.obtainFree(29));
    }

}