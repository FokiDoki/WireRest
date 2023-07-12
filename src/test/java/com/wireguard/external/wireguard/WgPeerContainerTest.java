package com.wireguard.external.wireguard;

import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WgPeerContainerTest {
    WgPeerContainer container;
    @BeforeEach
    void setUp() {
        container = new WgPeerContainer(Set.of(
                WgPeer.publicKey("pub1").build(),
                WgPeer.publicKey("pub2").build(),
                WgPeer.publicKey("pub3").presharedKey("psk1").build(),
                WgPeer.publicKey("pub4").presharedKey("psk12").build(),
                WgPeer.publicKey("pub5").latestHandshake(1).build(),
                WgPeer.publicKey("pub6").latestHandshake(2).build(),
                WgPeer.publicKey("pub7").latestHandshake(5).allowedIPv4Ips(Set.of("10.0.0.0/32")).build(),
                WgPeer.publicKey("pub8").allowedIPv4Ips(Set.of("10.0.0.1/32")).build(),
                WgPeer.publicKey("pub9").allowedIPv4Ips(Set.of("10.0.0.5/32")).build()


        ));
    }

    @Test
    void getByPublicKey() {
        Optional<WgPeer> peer = container.getByPublicKey("pub1");
        assertTrue(peer.isPresent());

    }

    @Test
    void removePeerByPublicKey() {
        int sizeBeforeDelete = container.size();
        container.removePeerByPublicKey("pub1");
        assertEquals(sizeBeforeDelete-1, container.size());
        assertTrue(container.getByPublicKey("pub1").isEmpty());
    }

    @Test
    void getByPresharedKey() {
        Optional<WgPeer> peer = container.getByPresharedKey("psk1");
        assertTrue(peer.isPresent());
        assertEquals("pub3", peer.get().getPublicKey());
    }

    @Test
    void getIpv4Addresses() {
        Set<String> ipv4Addresses = container.getIpv4Addresses();
        assertNotNull(ipv4Addresses);
        assertFalse(ipv4Addresses.isEmpty());
        assertEquals(3, ipv4Addresses.size());
        assertTrue(ipv4Addresses.contains("10.0.0.1/32"));
    }

    @Test
    void findAllPageable() {
        Page<WgPeer> page = container.findAll(PageRequest.of(1, 2, Sort.by("latestHandshake").descending()));
        assertNotNull(page);
        List<WgPeer> peers = page.getContent();
        assertEquals(2, peers.size());
        assertEquals(1, peers.get(0).getLatestHandshake());
        assertEquals(0, peers.get(1).getLatestHandshake());
    }

    @Test
    void findAllSort() {
        Iterable<WgPeer> peers = container.findAll(Sort.by("latestHandshake").descending());
        List<WgPeer> iter = (List<WgPeer>) IterableUtil.toCollection(peers);
        assertNotNull(peers);
        assertEquals(5, iter.get(0).getLatestHandshake());
        assertEquals(2, iter.get(1).getLatestHandshake());
        assertEquals(1, iter.get(2).getLatestHandshake());

    }

    @Test
    void testFindAll() {
    }
}