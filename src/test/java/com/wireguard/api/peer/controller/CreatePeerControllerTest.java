package com.wireguard.api.peer.controller;

import com.wireguard.api.peer.CreatedPeerDTO;
import com.wireguard.api.peer.testData;
import com.wireguard.external.network.NoFreeIpException;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.wireguard.IpAllocationRequestOneIfNullSubnets;
import com.wireguard.external.wireguard.PeerCreationRequest;
import com.wireguard.external.wireguard.peer.CreatedPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Set;

import static org.hamcrest.Matchers.containsString;

@WebFluxTest(CreatePeerController.class)
class CreatePeerControllerTest {

    @MockBean
    WgPeerService wgPeerService;
    @Autowired
    private WebTestClient webClient;
    private final static String BASE_URL = "/v1/peers";


    @Test
    void createPeer() {
        CreatedPeer newPeer = new CreatedPeer(
                "PubKey3",
                "PresharedKey3",
                "PrivateKey3",
                Set.of(Subnet.valueOf("10.0.0.0/32")),
                0
        );
        Mockito.when(wgPeerService.createPeerGenerateNulls(Mockito.any())).thenReturn(newPeer);
        webClient.post().uri(BASE_URL).exchange()
                .expectStatus().isCreated()
                .expectBody(CreatedPeerDTO.class)
                .isEqualTo(CreatedPeerDTO.from(newPeer));
    }

    @Test
    void createPeerWithParams() {
        CreatedPeer newPeer = new CreatedPeer(
                testData.getFakePubKey(),
                testData.getFakePubKey(),
                testData.getFakePubKey(),
                Set.of(Subnet.valueOf("10.0.0.10/32")),
                25);
        Mockito.when(wgPeerService.createPeerGenerateNulls(new PeerCreationRequest(newPeer.getPublicKey(),
                newPeer.getPresharedKey(),
                newPeer.getPrivateKey(),
                new IpAllocationRequestOneIfNullSubnets(newPeer.getAllowedSubnets()),
                25))).thenReturn(newPeer);
        webClient.post().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("publicKey", newPeer.getPublicKey())
                        .queryParam("presharedKey", newPeer.getPresharedKey())
                        .queryParam("privateKey", newPeer.getPrivateKey())
                        .queryParam("allowedIps", newPeer.getAllowedSubnets())
                        .queryParam("persistentKeepalive", 25)
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreatedPeerDTO.class)
                .isEqualTo(CreatedPeerDTO.from(newPeer));
    }

    @Test
    void createPeerWhenNoFreeIps() {
        Mockito.when(wgPeerService.createPeerGenerateNulls(Mockito.any())).thenThrow(new NoFreeIpException("No free ip"));
        webClient.post().uri(BASE_URL).exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").value(containsString("ip"));
    }

}