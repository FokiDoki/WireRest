package com.wireguard.containers;

import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.WgPeerContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class WgPeerContainerTest {
    WgPeerContainer wgPeerContainer = new WgPeerContainer();
    @BeforeEach
    public void setUp() {
        WgPeer peer1 = WgPeer.publicKey("publicKey1")
                        .presharedKey("presharedKey1")
                        .endpoint("192.168.0.1:2222")
                        .allowedIPv4Ips(Set.of("0.0.0.1/32"))
                        .latestHandshake(12345678)
                        .transferRx(2222)
                        .transferTx(1111)
                        .persistentKeepalive(0)
                        .build();
        WgPeer peer2 = WgPeer.publicKey("publi+c/Key=2")
                        .presharedKey("presharedKey2")
                        .endpoint("192.168.0.1:2222")
                        .allowedIPv4Ips(Set.of("0.0.0.2/32"))
                        .latestHandshake(12345678)
                        .transferRx(2222)
                        .transferTx(1111)
                        .persistentKeepalive(0)
                        .build();
        wgPeerContainer.add(peer1);
        wgPeerContainer.add(peer2);
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
