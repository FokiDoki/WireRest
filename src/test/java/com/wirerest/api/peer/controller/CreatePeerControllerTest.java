package com.wirerest.api.peer.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.wirerest.api.peer.CreatedPeerDTO;
import com.wirerest.api.peer.testData;
import com.wirerest.network.NoFreeIpInRange;
import com.wirerest.network.Subnet;
import com.wirerest.wireguard.peer.CreatedPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import com.wirerest.wireguard.peer.requests.IpAllocationRequestOneIfNullSubnets;
import com.wirerest.wireguard.peer.requests.PeerCreationRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;

@WebFluxTest(controllers = CreatePeerController.class,  excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
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

    @SneakyThrows
    @Test
    void createPeerWithParams() {
        JsonMapper jsonMapper = new JsonMapper();
        CreatedPeer newPeer = new CreatedPeer(
                testData.getFakePubKey(),
                testData.getFakePubKey(),
                testData.getFakePubKey(),
                Set.of(Subnet.valueOf("10.0.0.10/32")),
                25);
        Map<String, Object> request = Map.of(
                "publicKey", newPeer.getPublicKey(),
                "presharedKey", newPeer.getPresharedKey(),
                "privateKey", newPeer.getPrivateKey(),
                "allowedIps", newPeer.getAllowedSubnets().stream().map(Object::toString).toArray(),
                "persistentKeepalive", newPeer.getPersistentKeepalive());
        Mockito.when(wgPeerService.createPeerGenerateNulls(new PeerCreationRequest(newPeer.getPublicKey(),
                newPeer.getPresharedKey(),
                newPeer.getPrivateKey(),
                new IpAllocationRequestOneIfNullSubnets(newPeer.getAllowedSubnets()),
                25))).thenReturn(newPeer);
        webClient.post().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .build())
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreatedPeerDTO.class)
                .isEqualTo(CreatedPeerDTO.from(newPeer));
    }

    @Test
    void createPeerWhenNoFreeIps() {
        Mockito.when(wgPeerService.createPeerGenerateNulls(Mockito.any())).thenThrow(new NoFreeIpInRange("1","1"));
        webClient.post().uri(BASE_URL).exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").value(containsString("ip"));
    }

}