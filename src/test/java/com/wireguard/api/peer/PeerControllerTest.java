package com.wireguard.api.peer;

import com.wireguard.external.wireguard.NoFreeIpException;
import com.wireguard.external.wireguard.WgManager;
import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PeerControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    MockMvc mockMvc;

    @MockBean
    WgManager wgManager;
    Set<WgPeerDTO> peerDTOSet = new TreeSet<>(Comparator.comparing(WgPeerDTO::getPublicKey));
    public PeerControllerTest(){
        peerDTOSet.addAll(Set.of(
                WgPeerDTO.from(WgPeer.withPublicKey("PubKey1").build()),
                WgPeerDTO.from(WgPeer.withPublicKey("PubKey2")
                        .presharedKey("PresharedKey2")
                        .allowedIPv4Ips(Set.of("10.0.0.1/32","10.1.1.1/30"))
                        .allowedIPv6Ips(Set.of("2001:db8::/32"))
                        .transferTx(100)
                        .transferRx(200)
                        .latestHandshake(300)
                        .endpoint("1.1.1.1")
                        .build()
                )
                )
        );
    }

    @Test
    void getPeers() throws Exception {
        Mockito.when(wgManager.getPeers()).thenReturn(peerDTOSet);
        mockMvc.perform(get("/peers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].publicKey").isString())
                .andExpect(jsonPath("$[1].presharedKey").exists())
                .andExpect(jsonPath("$[1].endpoint").exists())
                .andExpect(jsonPath("$[1].allowedIps").isArray())
                .andExpect(jsonPath("$[1].latestHandshake").isNumber())
                .andExpect(jsonPath("$[1].transferRx").isNumber())
                .andExpect(jsonPath("$[1].transferTx").isNumber())
                .andExpect(jsonPath("$[1].persistentKeepalive").isNumber());
    }

    @Test
    void getPeerByPublicKey() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("publicKey", "PubKey2");
        Mockito.when(wgManager.getPeerDTOByPublicKey("PubKey2"))
                        .thenReturn(
                                Optional.of(
                                        peerDTOSet.stream().filter(p -> p.getPublicKey().equals("PubKey2"))
                                                .findFirst().get()
                                )
                        );
        mockMvc.perform(get("/peer").params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicKey").isString())
                .andExpect(jsonPath("$.presharedKey").isString())
                .andExpect(jsonPath("$.endpoint").isString())
                .andExpect(jsonPath("$.allowedIps").isArray())
                .andExpect(jsonPath("$.latestHandshake").isNumber())
                .andExpect(jsonPath("$.transferRx").isNumber())
                .andExpect(jsonPath("$.transferTx").isNumber())
                .andExpect(jsonPath("$.persistentKeepalive").isNumber());
    }

    @Test
    void getPeerByPublicKeyNotFound() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("publicKey", "NotExistedPubKey");
        Mockito.when(wgManager.getPeerDTOByPublicKey("NotExistedPubKey"))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/peer").params(params))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void createPeer() throws Exception {
        Mockito.when(wgManager.createPeer()).thenReturn(
                new CreatedPeer(
                        "PubKey3",
                        "PresharedKey3",
                            "PrivateKey3",
                        Set.of("10.0.0.0/32"),
                        0
                )
        );
        mockMvc.perform(post("/peer/create"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publicKey").isString())
                .andExpect(jsonPath("$.presharedKey").isString())
                .andExpect(jsonPath("$.privateKey").isString())
                .andExpect(jsonPath("$.address").isArray())
                .andExpect(jsonPath("$.persistentKeepalive").isNumber());
    }

    @Test
    void createPeerWhenNoFreeIps() throws Exception {
        Mockito.when(wgManager.createPeer()).thenThrow(new NoFreeIpException("No free ip"));
        /*assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/peer/create"));
        });*/
        mockMvc.perform(post("/peer/create"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("ip")));
    }
}