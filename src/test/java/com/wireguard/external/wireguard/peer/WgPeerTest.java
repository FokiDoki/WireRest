package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.Subnet;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WgPeerTest {

    @Test
    public void testBuilder(){
        WgPeer peer = WgPeer.publicKey("publicKey").build();
        assertEquals("publicKey", peer.getPublicKey());
    }

    @Test
    public void testBuilderAllArgs(){
        WgPeer peer = WgPeer.publicKey("pubkey")
                .presharedKey("presharedKey")
                .endpoint("endpoint")
                .allowedIps(Set.of(Subnet.valueOf("10.66.66.66/32")))
                .latestHandshake(1)
                .transferRx(2)
                .transferTx(3)
                .persistentKeepalive(4)
                .build();
        assertEquals("pubkey", peer.getPublicKey());
        assertEquals("presharedKey", peer.getPresharedKey());
        assertEquals("endpoint", peer.getEndpoint());
        assertEquals(1, peer.getLatestHandshake());
        assertEquals(2, peer.getTransferRx());
        assertEquals(3, peer.getTransferTx());
        assertEquals(4, peer.getPersistentKeepalive());
        assertEquals(Set.of(Subnet.valueOf("10.66.66.66/32")), peer.getAllowedSubnets().getIPv4Subnets());
    }

    @Test
    void testCompareAndEmptyAllowedSubnets(){
        WgPeer.AllowedSubnets allowedSubnets = new WgPeer.AllowedSubnets();
        assertTrue(allowedSubnets.isEmpty());
        allowedSubnets.addIpv4("0.0.0.32/32");
        assertFalse(allowedSubnets.isEmpty());
        allowedSubnets.addIpv6("::/0");
        assertFalse(allowedSubnets.isEmpty());
        WgPeer.AllowedSubnets allowedSubnets2 = new WgPeer.AllowedSubnets();
        assertEquals(1, allowedSubnets.compareTo(allowedSubnets2));
        allowedSubnets2.addIpv4("0.0.0.33/32");
        assertEquals(-1, allowedSubnets.compareTo(allowedSubnets2));
    }

}