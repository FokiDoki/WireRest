package com.wireguard.api.peer.controller;

import com.wireguard.api.ResourceNotFoundException;
import com.wireguard.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.api.peer.testData;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static com.wireguard.api.peer.testData.getFakePubKey;
import static org.hamcrest.Matchers.containsString;

@WebFluxTest(FindPeerController.class)
class FindPeerControllerTest {

    @Autowired
    private WebTestClient webClient;
    WgPeerDTOFromWgPeerConverter peerDTOC = new WgPeerDTOFromWgPeerConverter();
    private final static String BASE_URL = "/v1/peers";

    @MockBean
    WgPeerService wgPeerService;


    @Test
    void getPeerByPublicKey() {
        Optional<WgPeer> peer = Optional.of(
                testData.getPeers().stream().filter(p -> p.getPublicKey().equals(getFakePubKey()))
                        .findFirst().get()
        );
        Mockito.when(wgPeerService.getPeerByPublicKeyOrThrow(getFakePubKey()))
                .thenReturn(peer.get());
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/find")
                        .queryParam("publicKey", getFakePubKey())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(WgPeerDTO.class).isEqualTo(peerDTOC.convert(peer.get()));
    }

    @Test
    void getPeerByPublicKeyNotFound() {
        Mockito.when(wgPeerService.getPeerByPublicKeyOrThrow(getFakePubKey()))
                .thenThrow(new ResourceNotFoundException("not found"));
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/find")
                        .queryParam("publicKey", getFakePubKey())
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(404)
                .jsonPath("$.message").value(containsString("not found"));
    }


}