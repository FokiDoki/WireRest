package com.wireguard.api.peer.controller;

import com.wireguard.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.api.peer.testData;
import com.wireguard.external.wireguard.Paging;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.containsString;

@WebFluxTest(GetPeersController.class)
class GetPeersControllerTest {

    @MockBean
    WgPeerService wgPeerService;
    @Autowired
    private WebTestClient webClient;
    private final static String BASE_URL = "/v1/peers";
    List<WgPeer> peerList = testData.getPeers();
    WgPeerDTOFromWgPeerConverter peerDTOC = new WgPeerDTOFromWgPeerConverter();


    Paging<WgPeer> paging = new Paging<>(WgPeer.class);

    @Test
    void getPeers() {
        Page<WgPeer> expected = paging.apply(Pageable.ofSize(2), peerList);
        Mockito.when(wgPeerService.getPeers(Mockito.any())).thenReturn(expected);

        Iterator<WgPeer> peersIter = peerList.iterator();
        webClient.get().uri(BASE_URL).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].publicKey").isEqualTo(peersIter.next().getPublicKey())
                .jsonPath("$.content[1].presharedKey").isEqualTo(peersIter.next().getPresharedKey());
    }

    @Test
    void getPeersWithPageable() {
        List<WgPeer> peers = peerList.stream().toList().subList(0, 2);
        Mockito.when(wgPeerService.getPeers(PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "PresharedKey"))))
                .thenReturn(paging.apply(Pageable.ofSize(20), peerList));
        PageDTO<WgPeerDTO> expected = new PageDTO<>(
                1,
                2,
                peers.stream().map(peerDTOC::convert).toList()
        );
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("page", 1)
                        .queryParam("limit", 2)
                        .queryParam("sort", "PresharedKey.asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageDTO.class);
    }

    @Test
    void getPeersWithWrongPage() {
        Mockito.when(wgPeerService.getPeers(PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "WrongField"))))
                .thenThrow(new ParsingException("WrongField", new RuntimeException()));
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("page", -1)
                        .queryParam("limit", 2)
                        .queryParam("sort", "PresharedKey.asc")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").value(containsString("page"));
    }

    @Test
    void getPeersWithWrongLimit() {
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("page", 0)
                        .queryParam("limit", -1)
                        .queryParam("sort", "PresharedKey.asc")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").value(containsString("limit"));
    }

    @Test
    void getPeersWithWrongSortKey() {
        Mockito.when(wgPeerService.getPeers(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "WrongKey"))))
                .thenThrow(new ParsingException("WrongField", new RuntimeException()));
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("page", 0)
                        .queryParam("limit", 1)
                        .queryParam("sort", "WrongKey.asc")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").value(containsString("Field"));
    }

}