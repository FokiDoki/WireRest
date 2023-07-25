package com.wireguard.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.peer.CreatedPeerDTO;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.external.wireguard.PageOutOfRangeException;
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
    public OpenAPIExamplesConfigurator(ExamplesCustomizer examplesCustomizer) {
        this.examplesCustomizer = examplesCustomizer;
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

    private CreatedPeerDTO createdPeerDTO(){
        return new CreatedPeerDTO(
                "AhM4WLR7ETzLYDQ0zEq/0pvbYAxsbLwzzlIAdWhR7yg=",
                "q2MpyyqfAarG+zoVztTJk9ykQCVmOdBePtcmwZEc2iY=",
                "+EWn9NeR2pVuFHihYMC6LKreccd5VIW4prUkzHLy0nw=",
                Set.of("10.1.21.235/32"),
                0
        );
    }

    @Bean
    public OpenApiCustomizer openApiExamplesCustomizer(ExamplesCustomizer examplesCustomizer) {
        return openApi -> {
            openApi.getComponents().setExamples(examplesCustomizer.getMap());
        };
    }

    private void addObject(String key, String summary, Object object){
        examplesCustomizer.put(key,
                new Example().summary(summary).value(object)
        );
    }

    private void addError(String key, String summary, int code, String errorMessage){
        addObject(key, summary,
                new AppError(code, errorMessage));
    }

    @PostConstruct
    public void invalidPubKey() {
        addError("InvalidPubKey400", "Invalid public key", 400,
                "publicKey.value: Invalid key format (Base64 required) (test provided), " +
                        "publicKey.value: Key must be 44 characters long (test provided)");
    }

    @PostConstruct
    public void invalidPage() {
        PageOutOfRangeException ex = new PageOutOfRangeException(101, 100);
        addError("InvalidPage400", "Invalid Page", 400,
                ex.getMessage());
    }


    @PostConstruct
    public void unexpectedError(){
        addError("UnexpectedError500", "Unexpected error", 500,
                "Error text");
    }

    @PostConstruct
    public void rangeNoFreeIp(){
        addError("RangeNoFreeIp500", "No free ip", 500,
                "Range 10.0.0.0 - 10.0.0.255 has no free ip that can be assigned");
    }

    @PostConstruct
    public void pageWithPeers(){
        addObject("PageWithPeers", "Page with limit 1",
                new PageDTO<>(100, 0, List.of(constructPeerWithAllFields()))
        );
    }

    @PostConstruct
    public void peer(){
        addObject("peer", "Peer", constructPeerWithAllFields());
    }

    @PostConstruct
    public void peerAlreadyExists(){
        addError("peerAlreadyExists409", "Peer already exists", 409,
                "Peer with public key cHViQ0F4Tnc9PUZha2VQdWJLZXkgICAgICAgICAxOA== already exists");
    }

    @PostConstruct
    public void peerCreated(){
        addObject("createdPeer", "Created peer", createdPeerDTO());
    }
}
