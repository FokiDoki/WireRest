package com.wireguard.external.wireguard.converters;

import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.api.peer.WgPeerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WgPeerIterableToWgPeerDTOListTest {

    WgPeerIterableToWgPeerDTOList converter = new WgPeerIterableToWgPeerDTOList();
    @Test
    void convert() {
        List<WgPeerDTO> peers = converter.convert(Set.of(WgPeer.publicKey("").build()));
        Assertions.assertNotNull(peers);
        Assertions.assertFalse(peers.isEmpty());
        assertEquals(1, peers.size());

    }
}