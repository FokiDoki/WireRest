package com.wireguard.api.inteface;

import com.wireguard.external.wireguard.peer.WgPeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(InterfaceController.class)
class InterfaceControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    WgPeerService wgPeerService;
/*
    @Test
    void getInterface() {
        WgInterfaceDTO wgInterface = new WgInterfaceDTO("PrivateKey", "PublicKey", 1234, 5678);
        Mockito.when(wgPeerService.getInterface()).thenReturn(wgInterface);
        webClient.get().uri("/interface").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectBody(WgInterfaceDTO.class).isEqualTo(wgInterface);

    }*/
}