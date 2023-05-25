package com.wireguard.external.wireguard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubnetTest {
    @Test
    public void numericIpConvertTest2() {
        Subnet subnet = Subnet.fromString("0.1.0.0/24");
        assertEquals(65536L, subnet.getFirstIpNumeric(), ".getIpNumeric()");
    }
    @Test
    public void numericIpConvertTest3() {
        Subnet subnet = Subnet.fromString("0.0.1.0/24");
        assertEquals(256L, subnet.getFirstIpNumeric(), ".getIpNumeric()");
    }
    @Test
    public void testFromString() {
        Subnet subnet = Subnet.fromString("192.168.0.3/24");
        assertEquals("192.168.0.255", subnet.getLastIpString(), ".getLastIpString()");
        assertEquals("192.168.0.0", subnet.getFirstIpString(), ".getFirstIpString()");
        assertEquals(256, subnet.getIpCount(), ".getIpCount()");
        assertEquals(24, subnet.getNumericMask(), ".getNumericMask()");
        assertEquals(3232235775L, subnet.getLastIpNumeric(), ".getLastIpNumeric()");
        assertEquals(3232235520L, subnet.getFirstIpNumeric(), ".getFirstIpNumeric()");
    }

    @Test
    public void largeSubnetTest() {
        Subnet subnet = Subnet.fromString("0.0.0.0/0");
        assertEquals("0.0.0.0", subnet.getFirstIpString(), ".getFirstIpString()");
        assertEquals("255.255.255.255", subnet.getLastIpString(), ".getLastIpString()");
        assertEquals(4294967296L, subnet.getIpCount(), ".getIpCount()");
        assertEquals(0, subnet.getNumericMask(), ".getNumericMask()");
        assertEquals(0L, subnet.getFirstIpNumeric(), ".getFirstIpNumeric()");
        assertEquals(4294967295L, subnet.getLastIpNumeric(), ".getLastIpNumeric()");
    }

    @Test
    public void testFromBadStringWrongIp() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.fromString("192.999.0.3/24"));
    }

    @Test
    public void testFromBadString() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.fromString("hello world"));
    }

    @Test
    public void testFromNullString() {
        assertThrows(NullPointerException.class, () -> Subnet.fromString(null));
    }

    @Test
    public void testFromNullStringWrongMaskLess0() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.fromString("192.168.0.1/-1"));
    }

    @Test
    public void testFromNullStringWrongMaskMore32() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.fromString("192.168.0.1/33"));
    }



}