package com.wireguard.external.wireguard;

import com.wireguard.external.network.SubnetSolver;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.network.Subnet;
import com.wireguard.api.inteface.WgInterfaceDTO;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.external.wireguard.peer.CreatedPeer;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;

class WgPeerServiceTest {
    private WgPeerService wgPeerService;
    private static WgTool wgTool;
    final static String wgInterfaceName = "wg0";
    final static WgInterfaceDTO wgInterfaceDTO = new WgInterfaceDTO(
            "privateKey",
            "publicKey",
            1234,
            321
    );
    /*
    final static Set<WgPeer> peers = Set.of(
            WgPeer.publicKey("pubKey1")
                    .presharedKey("presharedKey1")
                    .allowedIPv4Subnets(Set.of("10.0.0.1/32"))
                    .latestHandshake(0)
                    .transferRx(0)
                    .transferTx(0)
                    .build(),
            WgPeer.publicKey("pubKey2").build(),
            WgPeer.publicKey("pubKey3")
                    .allowedIPv4Subnets(Set.of("10.0.0.0/32"))
                    .build()
    );
    @BeforeEach
    void setUp() throws IOException {
        wgTool = Mockito.mock(WgTool.class);
        Mockito.when(wgTool.showDump(wgInterfaceName)).thenReturn(
                new WgShowDump(wgInterfaceDTO, peers)
        );
        Mockito.when(wgTool.generatePrivateKey()).thenReturn("generatedPrivateKey");
        Mockito.when(wgTool.generatePresharedKey()).thenReturn("generatedPsk");
        Mockito.when(wgTool.generatePublicKey("generatedPrivateKey")).thenReturn("GeneratedPubKeyFromPrivateKey");

        NetworkInterfaceDTO wgInterface = new NetworkInterfaceDTO(wgInterfaceName);
        SubnetSolver subnetSolver = new SubnetSolver(Subnet.valueOf("10.0.0.0/16"));
        subnetSolver.obtain(Subnet.valueOf("10.0.0.0/31"));

        wgPeerService = new WgPeerService(
                wgTool,
                subnetSolver,
                wgInterface);
    }

    @AfterAll
    static void tearDown() {
        reset(wgTool);
    }


    @Test
    void getInterface() {
        WgInterfaceDTO wgInterface = wgPeerService.getInterface();
        assertEquals(wgInterfaceDTO, wgInterface);
    }

    @Test
    void getPeerByPublicKey() {
        Optional<WgPeerDTO> peer = wgPeerService.getPeerDTOByPublicKey("pubKey1");
        assertTrue(peer.isPresent());
        assertEquals(WgPeerDTO.from(peers.stream().filter(p -> p.getPublicKey().equals("pubKey1")).findFirst().get()), peer.get());
    }

    @Test
    void getPeers() {
        Set<WgPeerDTO> ManagerPeers = wgPeerService.getPeers();
        assertEquals(peers.size(), ManagerPeers.size());
        assertTrue(peers.stream().allMatch(Objects::nonNull));
    }

    @Test
    void createPeer() {
        CreatedPeer peer = wgPeerService.createPeer();
        assertEquals(Set.of("10.0.0.2/32"), peer.getAddress());
        assertEquals("generatedPsk", peer.getPresharedKey());
        assertEquals("GeneratedPubKeyFromPrivateKey", peer.getPublicKey());
        assertEquals("generatedPrivateKey", peer.getPrivateKey());
    }*/
}