package com.wirerest.api;

import com.wirerest.api.dto.PageDTO;
import com.wirerest.api.inteface.WgInterfaceDTO;
import com.wirerest.api.peer.CreatedPeerDTO;
import com.wirerest.api.peer.WgPeerDTO;
import com.wirerest.logs.LoggingEventDto;
import com.wirerest.network.AlreadyUsedException;
import com.wirerest.network.Subnet;
import com.wirerest.wireguard.PageOutOfRangeException;
import com.wirerest.wireguard.peer.PeerNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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
public class OpenAPIConfigurator {
    private final ExamplesCustomizer examplesCustomizer;

    @Autowired
    public OpenAPIConfigurator(ExamplesCustomizer examplesCustomizer) {
        this.examplesCustomizer = examplesCustomizer;
    }

    private WgPeerDTO constructPeerWithAllFields() {
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

    private CreatedPeerDTO createdPeerDTO() {
        return new CreatedPeerDTO(
                "AhM4WLR7ETzLYDQ0zEq/0pvbYAxsbLwzzlIAdWhR7yg=",
                "q2MpyyqfAarG+zoVztTJk9ykQCVmOdBePtcmwZEc2iY=",
                "+EWn9NeR2pVuFHihYMC6LKreccd5VIW4prUkzHLy0nw=",
                Set.of("10.1.21.235/32"),
                0
        );
    }

    private WgInterfaceDTO getInterfaceDTO() {
        return new WgInterfaceDTO(
                "iNpmL6pHFpBjOTKttQ2zfljJ6nMlyAeN1Xd7jQNVLGs=",
                "YFsZ0UjLVPeFOKZhWiVBVQMPnObwY0tuXLtjPfbqmF8=",
                51820,
                0);

    }

    @Bean
    public OpenApiCustomizer openApiExamplesCustomizer(ExamplesCustomizer examplesCustomizer) {
        return openApi -> {
            openApi.getComponents().setExamples(examplesCustomizer.getMap());
        };
    }

    @Bean
    public OpenApiCustomizer openApiDefaultCodes() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                ApiResponses apiResponses = operation.getResponses();
                addApiResponseNoOverride(apiResponses, "500",
                        createApiResponse("Server Error",
                                schemas.get(AppError.class.getSimpleName()),
                                Map.of("UnexpectedError500", examplesCustomizer.getMap().get("UnexpectedError500"))
                        )
                );

            }));
        };
    }

    private void addApiResponseNoOverride(ApiResponses apiResponses, String code, ApiResponse apiResponse) {
        if (apiResponses.containsKey(code)) {
            MediaType newMediaType = apiResponse.getContent().get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
            MediaType oldMediaType = apiResponses.get(code).getContent().get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
            Map<String, Example> oldExamples = Objects.requireNonNullElse(
                    oldMediaType.getExamples(),
                    new HashMap<>());
            Map<String, Example> newExamples = Objects.requireNonNullElse(
                    newMediaType.getExamples(),
                    new HashMap<>());
            oldExamples.putAll(newExamples);
            setExamples(apiResponse, oldExamples);
        }
        apiResponses.addApiResponse(code, apiResponse);
    }

    private ApiResponse createApiResponse(String message, Schema schema, Map<String, Example> example) {
        ApiResponse apiResponse = createApiResponse(message, schema);
        setExamples(apiResponse, example);
        return apiResponse;
    }

    private ApiResponse createApiResponse(String message, Schema schema) {
        MediaType mediaType = new MediaType();
        mediaType.schema(schema);
        return new ApiResponse().description(message)
                .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType));
    }

    private void setExamples(ApiResponse apiResponse, Map<String, Example> examples) {
        apiResponse.getContent()
                .get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE).setExamples(examples);
    }


    private void addObject(String key, String summary, Object object) {
        examplesCustomizer.put(key,
                new Example().summary(summary).value(object)
        );
    }

    private void addError(String key, String summary, int code, String errorMessage) {
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
    public void unexpectedError() {
        addError("UnexpectedError500", "Unexpected error", 500,
                "Error text");
    }

    @PostConstruct
    public void rangeNoFreeIp() {
        addError("RangeNoFreeIp500", "No free ip", 500,
                "Range 10.0.0.0 - 10.0.0.255 has no free ip that can be assigned");
    }

    @PostConstruct
    public void pageWithPeers() {
        addObject("PageWithPeers", "Page with limit 1",
                new PageDTO<>(100, 0, List.of(constructPeerWithAllFields()))
        );
    }

    @PostConstruct
    public void peer() {
        addObject("peer", "Peer", constructPeerWithAllFields());
    }

    @PostConstruct
    public void peerAlreadyExists() {
        addError("peerAlreadyExists409", "Peer already exists", 409,
                "Peer with public key cHViQ0F4Tnc9PUZha2VQdWJLZXkgICAgICAgICAxOA== already exists");
    }

    @PostConstruct
    public void peerCreated() {
        addObject("createdPeer", "Created peer", createdPeerDTO());
    }

    @PostConstruct
    public void peerNotFound() {
        PeerNotFoundException ex = new PeerNotFoundException("cHViQ0F4Tnc9PUZha2VQdWJLZXkgICAgICAgICAxOA==");
        addError("peerNotFound", "Peer not found", 404,
                ex.getMessage());
    }

    @PostConstruct
    public void getInterface() {
        addObject("interface", "Interface", getInterfaceDTO());
    }

    @PostConstruct
    public void getLogs() {
        List<LoggingEventDto> loggingEventDtos = List.of(
                new LoggingEventDto("INFO", "Init duration for springdoc-openapi is: 529 ms", 1690301255231L),
                new LoggingEventDto("ERROR", "Peer with public key ALD3x7qWP0W/4zC26jFozxw28vXJsazA33KnHF+AfHw= not found", 1690301333239L)
        );
        addObject("logs", "Logs", loggingEventDtos);
    }

    @PostConstruct
    public void logsLimit() {
        addError("logsLimit400", "Invalid limit", 400,
                "getLogs.limit: must be greater than or equal to 0");
    }

    @PostConstruct
    public void alreadyUsedException() {
        AlreadyUsedException alreadyUsedException = new AlreadyUsedException(Subnet.valueOf("10.0.0.100/32"));
        addError("alreadyUsed409", "Already used", 409,
                alreadyUsedException.getMessage());
    }

}