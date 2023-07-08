package com.wireguard.api.peer;

import com.wireguard.api.AppError;
import com.wireguard.external.network.NoFreeIpException;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.WgManager;
import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;

@WebFluxTest(PeerController.class)
class PeerControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    WgManager wgManager;

    List<WgPeerDTO> peerDTOList = List.of(
            WgPeerDTO.from(WgPeer.publicKey("PubKey1").build()),
            WgPeerDTO.from(WgPeer.publicKey("PubKey2")
                    .presharedKey("PresharedKey2")
                    .allowedIPv4Ips(Set.of("10.0.0.1/32","10.1.1.1/30"))
                    .allowedIPv6Ips(Set.of("2001:db8::/32"))
                    .transferTx(100)
                    .transferRx(200)
                    .latestHandshake(300)
                    .endpoint("1.1.1.1")
                    .build())
    );


    @Test
    void getPeers() {
        Mockito.when(wgManager.getPeers((Pageable) Mockito.any())).thenReturn(peerDTOList);

        Iterator<WgPeerDTO> peersIter = peerDTOList.iterator();
        webClient.get().uri("/peers").exchange()
                .expectStatus().isOk()
                .expectBodyList(WgPeerDTO.class).hasSize(2)
                .contains(peersIter.next())
                .contains(peersIter.next());
    }

    @Test
    void getPeersWithPageable() {
        List<WgPeerDTO> peers = peerDTOList.stream().toList().subList(0,2);
        Mockito.when(wgManager.getPeers(PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "PresharedKey"))))
                .thenReturn(peers);
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/peers")
                        .queryParam("page", 1)
                        .queryParam("limit", 2)
                        .queryParam("sort", "PresharedKey.asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WgPeerDTO.class).hasSize(2)
                .contains(peers.get(0), peers.get(1));
    }

    @Test
    void getPeersWithWrongPage(){
        Mockito.when(wgManager.getPeers(PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "WrongField"))))
                .thenThrow(new ParsingException("WrongField", new RuntimeException()));
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/peers")
                        .queryParam("page", -1)
                        .queryParam("limit", 2)
                        .queryParam("sort", "PresharedKey.asc")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").value(containsString("Page"));
    }

    @Test
    void getPeersWithWrongLimit(){
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/peers")
                        .queryParam("page", 0)
                        .queryParam("limit", -1)
                        .queryParam("sort", "PresharedKey.asc")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(400)
                .jsonPath("$.message").value(containsString("size"));
    }

    @Test
    void getPeersWithWrongSortKey(){
        Mockito.when(wgManager.getPeers(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "WrongKey"))))
                .thenThrow(new ParsingException("WrongField", new RuntimeException()));
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/peers")
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



    @Test
    void getPeerByPublicKey() {
        Optional<WgPeerDTO> peer = Optional.of(
                peerDTOList.stream().filter(p -> p.getPublicKey().equals("PubKey2"))
                .findFirst().get()
        );
        Mockito.when(wgManager.getPeerDTOByPublicKey("PubKey2"))
                        .thenReturn(peer);
        webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/peer")
                        .queryParam("publicKey", "PubKey2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(WgPeerDTO.class).isEqualTo(peer.get());
    }

    @Test
    void getPeerByPublicKeyNotFound()  {
        Mockito.when(wgManager.getPeerDTOByPublicKey("NotExistedPubKey"))
                .thenReturn(Optional.empty());
        webClient.get().uri("/peer")
                .attribute("publicKey", "NotExistedPubKey")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(404)
                .jsonPath("$.message").value(containsString("not found"));
    }


    @Test
    void createPeer() {
        CreatedPeer newPeer = new CreatedPeer(
                "PubKey3",
                "PresharedKey3",
                "PrivateKey3",
                Set.of("10.0.0.0/32"),
                0
        );
        Mockito.when(wgManager.createPeerGenerateNulls(null, null, null, null, null)).thenReturn(newPeer);
        webClient.post().uri("/peer/create").exchange()
                .expectStatus().isCreated()
                .expectBody(CreatedPeer.class)
                .isEqualTo(newPeer);
    }

    @Test
    void createPeerWithParams() {
        CreatedPeer newPeer = new CreatedPeer(
                "PubKeyParams",
                "PresharedKeyParams",
                "PrivateKeyParams",
                Set.of("10.0.0.10/32"),
                25);
        Mockito.when(wgManager.createPeerGenerateNulls(newPeer.getPublicKey(),
                newPeer.getPresharedKey(),
                newPeer.getPrivateKey(),
                newPeer.getAddress().stream().map(Subnet::valueOf).collect(Collectors.toSet()),
                25)).thenReturn(newPeer);
        webClient.post().uri(uriBuilder -> uriBuilder
                        .path("/peer/create")
                        .queryParam("publicKey", newPeer.getPublicKey())
                        .queryParam("presharedKey", newPeer.getPresharedKey())
                        .queryParam("privateKey", newPeer.getPrivateKey())
                        .queryParam("address", newPeer.getAddress())
                        .queryParam("persistentKeepalive", 25)
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreatedPeer.class)
                .isEqualTo(newPeer);
    }

    @Test
    void createPeerWhenNoFreeIps() {
        Mockito.when(wgManager.createPeerGenerateNulls(null, null, null, null, null)).thenThrow(new NoFreeIpException("No free ip"));
        webClient.post().uri("/peer/create").exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(500)
                .jsonPath("$.message").value(containsString("ip"));
    }

    @Test
    void deletePeer() {
        WgPeerDTO peerToDelete = peerDTOList.get(1);
        Mockito.when(wgManager.getPeerDTOByPublicKey("PubKey2")).thenReturn(Optional.of(peerToDelete));
        webClient.delete().uri(uriBuilder -> uriBuilder
                        .path("/peer/delete")
                        .queryParam("publicKey", "PubKey2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(WgPeerDTO.class)
                .isEqualTo(peerToDelete);
    }

    @Test
    void deletePeerNotExists(){
        Mockito.when(wgManager.getPeerDTOByPublicKey(Mockito.anyString())).thenReturn(Optional.empty());
        webClient.delete().uri(uriBuilder -> uriBuilder
                        .path("/peer/delete")
                        .queryParam("publicKey", "PubKey2")
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(AppError.class)
                .isEqualTo(new AppError(404, "Peer with public key PubKey2 not found"));
    }


}