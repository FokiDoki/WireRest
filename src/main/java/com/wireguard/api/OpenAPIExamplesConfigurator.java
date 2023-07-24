package com.wireguard.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.peer.WgPeerDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.examples.Example;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.SpringDocProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

@OpenAPIDefinition(

        info = @Info(
                title = "WireRest",
                description = "Docs for Wireguard API",
                version = "1",
                contact = @Contact(
                        name = "FokiDoki",
                        url = "https://github.com/FokiDoki/"
                )
        )
)

@Configuration
public class OpenAPIExamplesConfigurator {
    private final ExamplesCustomizer examplesCustomizer;
    @Autowired
    public OpenAPIExamplesConfigurator(ExamplesCustomizer examplesCustomizer, ObjectMapper om) {
        this.examplesCustomizer = examplesCustomizer;
        SpringDocProviders springDocProviders
    }

    private WgPeerDTO constructPeerWithAllFields(){
        WgPeerDTO peer = new WgPeerDTO();
        peer.setPublicKey("ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw=");
        peer.setPresharedKey("3hFqZXqzO+YkVL4nX2siavxK1Z3h5lRLkEQz1qf3giI=");
        peer.setEndpoint("123.23.2.3:55412");
        peer.setAllowedSubnets(Set.of("2002:0:0:1234::/64", "10.1.142.196/32"));
        peer.setLatestHandshake(1690200786);
        peer.setTransferRx(12345);
        peer.setTransferTx(54321);
        peer.setPersistentKeepalive(25);
        return peer;
    }

    private WgPeerDTO constructPeerWithMinimumFields(){
        WgPeerDTO peer = new WgPeerDTO();
        peer.setPublicKey("ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw=");
        peer.setPublicKey(null);
        peer.setPresharedKey(null);
        peer.setEndpoint(null);
        peer.setAllowedSubnets(Set.of());
        return peer;
    }


    @Bean
    public OpenApiCustomizer openApiExamplesCustomizer(ExamplesCustomizer examplesCustomizer) {
        return openApi -> {
            openApi.getComponents().setExamples(examplesCustomizer.getMap());
        };
    }

    @PostConstruct
    public void invalidPubKey() {
        examplesCustomizer.put(
                "InvalidPubKey400",
                new Example().summary("Invalid public key").value(
                        new AppError(400, "publicKey.value: Invalid key format (Base64 required) (test provided), " +
                                "publicKey.value: Key must be 44 characters long (test provided)")
                )
        );
    }

    @PostConstruct
    public void unexpectedError(){
        examplesCustomizer.put(
                "UnexpectedError500",
                new Example().summary("Unexpected error").value(
                        new AppError(500, "Error text")
                )
        );
    }

    @PostConstruct
    public void rangeNoFreeIp(){
        examplesCustomizer.put(
                "RangeNoFreeIp500",
                new Example().summary("No free ip").value(
                        new AppError(500, "Range 10.0.0.0 - 10.0.0.255 has no free ip that can be assigned")
                )
        );
    }

    @PostConstruct
    public void pageWithPeers(){
        examplesCustomizer.put(
                "PageWithPeers",
                 new Example().summary("One peer").value(
                         new PageDTO<>(100, 0, List.of(constructPeerWithAllFields()))
                 )
        );
    }

    @PostConstruct
    public void pageWithPeersWithNulls(){
        List<WgPeerDTO> peers = List.of(constructPeerWithMinimumFields(),
                constructPeerWithAllFields());
        Example example =                 new Example().summary("Peer with nulls").value(
                new PageDTO<>(100, 0, peers)
        );
        examplesCustomizer.put(
                "PageWithPeersWithNulls",
                example
        );
    }
}
