package com.wireguard.external.wireguard;

import com.wireguard.external.network.ISubnet;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.network.SubnetSolver;
import com.wireguard.external.wireguard.peer.*;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WgPeerServiceTest {
    private WgPeerService wgPeerService;

    WgPeerGenerator wgPeerGenerator;
    WgPeerRepository wgPeerRepository;
    SubnetSolver subnetSolver;

    @BeforeEach
    public void setup() {
        wgPeerRepository = Mockito.mock(WgPeerRepository.class);
        wgPeerGenerator = Mockito.mock(WgPeerGenerator.class);
        subnetSolver = Mockito.mock(SubnetSolver.class);
        wgPeerService = new WgPeerService(wgPeerGenerator, wgPeerRepository, subnetSolver,new PeerCreationRules(32));
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
        PeerCreationRequest peerCreationRequest = new PeerCreationRequest("publicKey", "Psk", "Ppk",
                Set.of(), 0, 1);
        Mockito.when(wgPeerGenerator.createPeerGenerateNulls(
                        peerCreationRequest))
                .thenReturn(shouldBeReturned);
        CreatedPeer createdPeer = wgPeerService.createPeerGenerateNulls(peerCreationRequest);
        Assertions.assertNotNull(createdPeer);
        Assertions.assertEquals(shouldBeReturned, createdPeer);
        Mockito.verify(wgPeerGenerator, Mockito.times(1)).createPeerGenerateNulls(
                new PeerCreationRequest(shouldBeReturned.getPublicKey(), shouldBeReturned.getPresharedKey(), shouldBeReturned.getPrivateKey(),
                Mockito.any(), shouldBeReturned.getPersistentKeepalive(), 1));
        Mockito.verify(wgPeerRepository, Mockito.times(1)).add(Mockito.any(WgPeer.class));
    }

    @Test
    public void testCreatePeer() {
        Mockito.when(wgPeerGenerator.createPeerGenerateNulls(new EmptyPeerCreationRequest()))
                .thenReturn(new CreatedPeer("publicKey", "Psk", "Ppk", Set.of(Subnet.valueOf("0.0.0.0/32")), 0));
        wgPeerService.createPeer();
        Mockito.verify(wgPeerGenerator, Mockito.times(1)).createPeerGenerateNulls(
                new EmptyPeerCreationRequest());
        Mockito.verify(wgPeerRepository, Mockito.times(1)).add(Mockito.any(WgPeer.class));
    }

    @Test
    public void deletePeer(){
        wgPeerService.deletePeer("publicKey");
        Mockito.verify(wgPeerRepository, Mockito.times(1)).remove(Mockito.any());
    }
}