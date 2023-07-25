package com.wireguard.api.inteface;

import com.wireguard.api.inteface.controller.GetInterfaceController;
import com.wireguard.external.wireguard.iface.WgInterface;
import com.wireguard.external.wireguard.iface.WgInterfaceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(GetInterfaceController.class)
class GetInterfaceControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    WgInterfaceService wgInterfaceService;

    @Test
    void getInterface() {
        WgInterface wgInterface = new WgInterface("PrivateKey", "PublicKey", 1234, 5678);
        Mockito.when(wgInterfaceService.getInterface()).thenReturn(wgInterface);
        webClient.get().uri("/v1/interface").accept(MediaType.APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectBody(WgInterfaceDTO.class).isEqualTo(WgInterfaceDTO.from(wgInterface));

    }
}