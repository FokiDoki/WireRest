package com.wireguard.containers;

import com.wireguard.external.wireguard.WgPeerContainer;
import com.wireguard.external.wireguard.WgPeer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

public class WgPeerContainerTest {
    WgPeerContainer wgPeerContainer = new WgPeerContainer();
    @BeforeEach
    public void setUp() {
        WgPeer peer1 = WgPeer.withPublicKey("publicKey1")
                        .presharedKey("presharedKey1")
                        .endpoint("192.168.0.1:2222")
                        .allowedIPv4Ips(Set.of("allowedIps1"))
                        .latestHandshake(12345678)
                        .transferRx(2222)
                        .transferTx(1111)
                        .persistentKeepalive(0)
                        .build();
        WgPeer peer2 = WgPeer.withPublicKey("publi+c/Key=2")
                        .presharedKey("presharedKey2")
                        .endpoint("192.168.0.1:2222")
                        .allowedIPv4Ips(Set.of("allowedIps1"))
                        .latestHandshake(12345678)
                        .transferRx(2222)
                        .transferTx(1111)
                        .persistentKeepalive(0)
                        .build();
        wgPeerContainer.addPeer(peer1);
        wgPeerContainer.addPeer(peer2);
    }

    @Test
    public void testIsPresent(){
        Assertions.assertTrue(wgPeerContainer.getByPublicKey("publicKey1").isPresent());
    }

    @Test
    public void testGetByPublicKey() {
        WgPeer wgPeer = wgPeerContainer.getByPublicKey("publicKey1").get();
        Assertions.assertEquals("publicKey1", wgPeer.getPublicKey());
    }
    @Test
    public void testGetByPublicKey2() {
        WgPeer wgPeer = wgPeerContainer.getByPublicKey("publi+c/Key=2").get();
        Assertions.assertEquals("publi+c/Key=2", wgPeer.getPublicKey());
    }

    @Test
    public void testGetByPreSharedKey() {
        WgPeer wgPeer = wgPeerContainer.getByPresharedKey("presharedKey1").get();
        Assertions.assertEquals("presharedKey1", wgPeer.getPresharedKey());
    }


}
