package com.wireguard.external.wireguard;

import com.wireguard.external.network.Subnet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubnetTest {
    @Test
    public void numericIpConvertTest2() {
        Subnet subnet = Subnet.valueOf("0.1.0.0/24");
        assertEquals(65536L, subnet.getFirstIpNumeric(), ".getIpNumeric()");
    }

    @Test
    public void numericIpConvertTest3() {
        Subnet subnet = Subnet.valueOf("0.0.1.0/24");
        assertEquals(256L, subnet.getFirstIpNumeric(), ".getIpNumeric()");
    }

    @Test
    public void testFromString() {
        Subnet subnet = Subnet.valueOf("192.168.0.3/24");
        assertEquals("192.168.0.255", subnet.getLastIpString(), ".getLastIpString()");
        assertEquals("192.168.0.0", subnet.getFirstIpString(), ".getFirstIpString()");
        assertEquals(256, subnet.getIpCount(), ".getIpCount()");
        assertEquals(24, subnet.getNumericMask(), ".getNumericMask()");
        assertEquals(3232235775L, subnet.getLastIpNumeric(), ".getLastIpNumeric()");
        assertEquals(3232235520L, subnet.getFirstIpNumeric(), ".getFirstIpNumeric()");
    }

    @Test
    public void largeSubnetTest() {
        Subnet subnet = Subnet.valueOf("0.0.0.0/0");
        assertEquals("0.0.0.0", subnet.getFirstIpString(), ".getFirstIpString()");
        assertEquals("255.255.255.255", subnet.getLastIpString(), ".getLastIpString()");
        assertEquals(4294967296L, subnet.getIpCount(), ".getIpCount()");
        assertEquals(0, subnet.getNumericMask(), ".getNumericMask()");
        assertEquals(0L, subnet.getFirstIpNumeric(), ".getFirstIpNumeric()");
        assertEquals(4294967295L, subnet.getLastIpNumeric(), ".getLastIpNumeric()");
    }

    @Test
    public void testFromBadStringWrongIp() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.valueOf("192.999.0.3/24"));
    }

    @Test
    public void testFromBadString() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.valueOf("hello world"));
    }

    @Test
    public void testFromNullString() {
        assertThrows(NullPointerException.class, () -> Subnet.valueOf(null));
    }

    @Test
    public void testFromNullStringWrongMaskLess0() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.valueOf("192.168.0.1/-1"));
    }

    @Test
    public void testFromNullStringWrongMaskMore32() {
        assertThrows(IllegalArgumentException.class, () -> Subnet.valueOf("192.168.0.1/33"));
    }

    @Test
    public void testGetLastIpNumeric() {
        Subnet subnet = Subnet.valueOf("0.0.0.0/17");
        assertEquals(32767L, subnet.getLastIpNumeric(), ".getLastIpNumeric()");
    }

    @Test
    public void getIpTest() {
        Subnet subnet = Subnet.valueOf("192.168.0.2/17");
        assertEquals(List.of(192,168,0,2), subnet.getIp(), ".getIp()");
    }

    @Test
    public void getFirstIpTest() {
        Subnet subnet = Subnet.valueOf("192.168.0.5/20");
        assertEquals(List.of(192,168,0,0), subnet.getFirstIp(), ".getFirstIp()");
    }

    @Test
    public void getLastIpTest() {
        Subnet subnet = Subnet.valueOf("192.168.0.5/20");
        assertEquals(List.of(192, 168, 15, 255), subnet.getLastIp(), ".getLastIp()");

    }


}