package com.wireguard.containers;

import com.wireguard.DTO.WgPeer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.sql.Timestamp;

public class WgPeerContainerTest {
    WgPeerContainer wgPeerContainer = new WgPeerContainer();
    @BeforeEach
    public void setUp() {
        wgPeerContainer.addPeer(new WgPeer("publicKey1",
                "presharedKey1", new InetSocketAddress("192.168.0.1", 2222),
                "allowedIps1", new Timestamp(12345678),
                2222, 1111, 0));
        wgPeerContainer.addPeer(new WgPeer("publi+c/Key=2",
                "presharedKey2", new InetSocketAddress("192.168.0.2", 2222),
                "allowedIps1", new Timestamp(12345678),
                2222, 1111, 0));
    }

    @Test
    public void testGetByPublicKey() {
        WgPeer wgPeer = wgPeerContainer.getByPublicKey("publicKey1");
        Assertions.assertEquals("publicKey1", wgPeer.getPublicKey());
    }
    @Test
    public void testGetByPublicKey2() {
        WgPeer wgPeer = wgPeerContainer.getByPublicKey("publi+c/Key=2");
        Assertions.assertEquals("publi+c/Key=2", wgPeer.getPublicKey());
    }

    @Test
    public void testGetByPreSharedKey() {
        WgPeer wgPeer = wgPeerContainer.getByPresharedKey("presharedKey1");
        Assertions.assertEquals("presharedKey1", wgPeer.getPresharedKey());
    }
}
