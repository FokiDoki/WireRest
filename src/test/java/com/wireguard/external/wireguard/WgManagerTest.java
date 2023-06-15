package com.wireguard.external.wireguard;

import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.reset;

class WgManagerTest {
    private WgManager wgManager;
    private static WgTool wgTool;
    final static String wgInterfaceName = "wg0";
    final static WgInterfaceDTO wgInterfaceDTO = new WgInterfaceDTO(
            "privateKey",
            "publicKey",
            1234,
            321
    );
    final static List<WgPeer> peers = List.of(
            WgPeer.withPublicKey("pubKey1")
                    .presharedKey("presharedKey1")
                    .allowedIPv4Ips(Set.of("10.0.0.1/32"))
                    .latestHandshake(0)
                    .transferRx(0)
                    .transferTx(0)
                    .build(),
            WgPeer.withPublicKey("pubKey2").build(),
            WgPeer.withPublicKey("pubKey3")
                    .allowedIPv4Ips(Set.of("10.0.0.0/32"))
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

        IpResolver ipResolver = new IpResolver(Subnet.fromString("10.0.0.0/16"));
        ipResolver.takeSubnet(Subnet.fromString("10.0.0.0/31"));

        wgManager = new WgManager(
                wgTool,
                ipResolver,
                new WgInterface(wgInterfaceName, "10.0.0.0"));
    }

    @AfterAll
    static void tearDown() {
        reset(wgTool);
    }


    @Test
    void getInterface() {
        WgInterfaceDTO wgInterface = wgManager.getInterface();
        assertEquals(wgInterfaceDTO, wgInterface);
    }

    @Test
    void getPeerByPublicKey() {
        Optional<WgPeerDTO> peer = wgManager.getPeerDTOByPublicKey("pubKey1");
        assertTrue(peer.isPresent());
        assertEquals(WgPeerDTO.from(peers.get(0)), peer.get());
    }

    @Test
    void getPeers() {
        Set<WgPeerDTO> ManagerPeers = wgManager.getPeers();
        assertEquals(peers.size(), ManagerPeers.size());
        assertTrue(peers.stream().allMatch(Objects::nonNull));
    }

    @Test
    void createPeer() {
        CreatedPeer peer = wgManager.createPeer();
        assertEquals(Set.of("10.0.0.2/32"), peer.getAddress());
        assertEquals("generatedPsk", peer.getPresharedKey());
        assertEquals("GeneratedPubKeyFromPrivateKey", peer.getPublicKey());
        assertEquals("generatedPrivateKey", peer.getPrivateKey());
    }
}