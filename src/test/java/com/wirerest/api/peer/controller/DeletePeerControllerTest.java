package com.wirerest.api.peer.controller;

import com.wirerest.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wirerest.api.peer.WgPeerDTO;
import com.wirerest.api.peer.testData;
import com.wirerest.wireguard.peer.WgPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.containsString;


@WebFluxTest(controllers = DeletePeerController.class,  excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class DeletePeerControllerTest {

    @MockBean
    WgPeerService wgPeerService;
    @Autowired
    private WebTestClient webClient;
    private final static String BASE_URL = "/v1/peers";
    List<WgPeer> peerList = testData.getPeers();
    WgPeerDTOFromWgPeerConverter peerDTOC = new WgPeerDTOFromWgPeerConverter();

    @Test
    void deletePeer() {
        WgPeer peerToDelete = peerList.get(1);
        Mockito.when(wgPeerService.deletePeer(Mockito.anyString())).thenReturn(peerToDelete);
        webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("publicKey", testData.getFakePubKey())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(WgPeerDTO.class)
                .isEqualTo(peerDTOC.convert(peerToDelete));
    }


    @Test
    void deletePeerNotExists() {
        Mockito.when(wgPeerService.deletePeer(Mockito.anyString())).thenThrow(new NoSuchElementException("not found"));
        webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("publicKey", testData.getFakePubKey())

                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(404)
                .jsonPath("$.message").value(containsString("not found"));

    }
}