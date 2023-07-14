package com.wireguard.external.wireguard;

import com.wireguard.api.inteface.WgInterfaceDTO;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.network.SubnetSolver;
import com.wireguard.external.wireguard.peer.*;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import com.wireguard.parser.WgShowDump;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WgPeerServiceTest {
    private WgPeerService wgPeerService;

    WgPeerCreator wgPeerCreator;
    WgPeerRepository wgPeerRepository;

    @BeforeEach
    public void setup() {
        wgPeerRepository = Mockito.mock(WgPeerRepository.class);
        wgPeerCreator = Mockito.mock(WgPeerCreator.class);
        wgPeerService = new WgPeerService(wgPeerCreator, wgPeerRepository);
    }

    @Test
    public void testGetPeerByPublicKeyFound() {
        Mockito.when(wgPeerRepository.getBySpecification(new FindByPublicKey("publicKey")))
                .thenReturn(List.of(WgPeer.publicKey("publicKey").build()));
        Optional<WgPeer> peer = wgPeerService.getPeerByPublicKey("publicKey");
        Assertions.assertTrue(peer.isPresent());
        WgPeer wgPeer = peer.get();
        assertEquals("publicKey", wgPeer.getPublicKey());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).getBySpecification(new FindByPublicKey("publicKey"));
    }

    @Test
    public void testGetPeerByPublicKeyNotFound() {
        Mockito.when(wgPeerRepository.getBySpecification(new FindByPublicKey("publicKey")))
                .thenReturn(List.of());
        Optional<WgPeer> peer = wgPeerService.getPeerByPublicKey("publicKey");
        Assertions.assertFalse(peer.isPresent());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).getBySpecification(new FindByPublicKey("publicKey"));
    }

    @Test
    public void testGetPeers() {
        Mockito.when(wgPeerRepository.getAll())
                .thenReturn(List.of(WgPeer.publicKey("publicKey").build()));
        List<WgPeer> peers = wgPeerService.getPeers();
        Assertions.assertEquals(1, peers.size());
        Assertions.assertNotNull(peers.get(0));
        Assertions.assertEquals("publicKey", peers.get(0).getPublicKey());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).getAll();
    }

    @Test
    public void testGetPeersWhenNoPeers() {
        Mockito.when(wgPeerRepository.getAll())
                .thenReturn(List.of());
        List<WgPeer> peers = wgPeerService.getPeers();
        Assertions.assertEquals(0, peers.size());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).getAll();
    }

    @Test
    public void testGetPeersPageable() {
        Mockito.when(wgPeerRepository.getAll(Pageable.ofSize(2)))
                .thenReturn(Page.empty());
        Page<WgPeer> page = wgPeerService.getPeers(Pageable.ofSize(2));
        Assertions.assertEquals(0, page.getTotalElements());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).getAll(Pageable.ofSize(2));
    }

    @Test
    public void testCreatePeerGenerateNulls() {
        CreatedPeer shouldBeReturned = new CreatedPeer("publicKey", "Psk", "Ppk", Set.of(Subnet.valueOf("10.66.66.1/32")), 0);
        Mockito.when(wgPeerCreator.createPeerGenerateNulls(
                new PeerCreationRequest("publicKey", "Psk", "Ppk",
                        null, 0, 1)))
                .thenReturn(shouldBeReturned);
        CreatedPeer createdPeer = wgPeerService.createPeerGenerateNulls(new PeerCreationRequest("publicKey", "Psk", "Ppk", null, 0, 1));
        Assertions.assertNotNull(createdPeer);
        Assertions.assertEquals(shouldBeReturned, createdPeer);
        Mockito.verify(wgPeerCreator, Mockito.times(1)).createPeerGenerateNulls(
                new PeerCreationRequest(shouldBeReturned.getPublicKey(), shouldBeReturned.getPresharedKey(), shouldBeReturned.getPrivateKey(),
                null, shouldBeReturned.getPersistentKeepalive(), 1));
        Mockito.verify(wgPeerRepository, Mockito.times(1)).add(Mockito.any(WgPeer.class));
    }

    @Test
    public void testCreatePeer() {
        Mockito.when(wgPeerCreator.createPeerGenerateNulls(new EmptyPeerCreationRequest()))
                .thenReturn(new CreatedPeer("publicKey", "Psk", "Ppk", Set.of(Subnet.valueOf("0.0.0.0/32")), 0));
        wgPeerService.createPeer();
        Mockito.verify(wgPeerCreator, Mockito.times(1)).createPeerGenerateNulls(
                new EmptyPeerCreationRequest());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).add(Mockito.any(WgPeer.class));
    }

    @Test
    public void deletePeer(){
        wgPeerService.deletePeer("publicKey");
        Mockito.verify(wgPeerRepository, Mockito.times(1)).remove(Mockito.any());
    }
}