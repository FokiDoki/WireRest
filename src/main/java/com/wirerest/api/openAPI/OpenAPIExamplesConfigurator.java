package com.wirerest.api.openAPI;

import com.wirerest.api.AppError;
import com.wirerest.api.inteface.WgInterfaceDTO;
import com.wirerest.api.security.InvalidTokenAppError;
import com.wirerest.api.service.StatsSnapshotDto;
import com.wirerest.logs.LoggingEventDto;
import com.wirerest.network.AlreadyUsedException;
import com.wirerest.network.Subnet;
import com.wirerest.wireguard.PageOutOfRangeException;
import com.wirerest.wireguard.peer.PeerNotFoundException;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class OpenAPIExamplesConfigurator {
    private final ExamplesCustomizer examplesCustomizer;

    @Autowired
    public OpenAPIExamplesConfigurator(ExamplesCustomizer examplesCustomizer) {
        this.examplesCustomizer = examplesCustomizer;
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
                addApiResponseNoOverride(apiResponses, "403",
                        createApiResponse("Invalid Token",
                                schemas.get(AppError.class.getSimpleName()),
                                Map.of("InvalidToken", examplesCustomizer.getMap().get("InvalidToken"))
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
        addObject("PageWithPeers", "Page with limit 1", new ExamplePageWithOnePeer());
    }

    @PostConstruct
    public void peer() {
        addObject("peer", "Peer", new ExamplePeerDTO());
    }

    @PostConstruct
    public void peerAlreadyExists() {
        addError("peerAlreadyExists409", "Peer already exists", 409,
                "Peer with public key cHViQ0F4Tnc9PUZha2VQdWJLZXkgICAgICAgICAxOA== already exists");
    }

    @PostConstruct
    public void peerCreated() {
        addObject("createdPeer", "Created peer", new ExampleCreatedPeerDTO());
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

    @PostConstruct
    public void stats() {
        StatsSnapshotDto statsSnapshotDto = new StatsSnapshotDto(1691322509881L, 50208,
                65536, 9562, 299314471044L, 300455868743L);
        addObject("stats", "Stats", statsSnapshotDto);
    }

    @PostConstruct
    public void InvalidToken() {
        addObject("InvalidToken", "Authorization token is invalid or not provided",
                new InvalidTokenAppError());
    }
}