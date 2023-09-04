package com.wirerest.api.peer.controller;

import com.wirerest.api.AppError;
import com.wirerest.api.converters.PeerCreationRequestFromDTOConverter;
import com.wirerest.api.openAPI.schemas.samples.PeerCreationRequestSchema;
import com.wirerest.api.peer.CreatedPeerDTO;
import com.wirerest.api.peer.EmptyPeerCreationRequestDTO;
import com.wirerest.api.peer.PeerCreationRequestDTO;
import com.wirerest.wireguard.peer.CreatedPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value = "v1/peers")
@Validated
public class CreatePeerController {

    private final WgPeerService wgPeerService;

    private final PeerCreationRequestFromDTOConverter creationRequestConverter = new PeerCreationRequestFromDTOConverter();

    public CreatePeerController(WgPeerService wgPeerService) {
        this.wgPeerService = wgPeerService;
    }

    @Operation(summary = "Create peer",
            description = """ 
                    Create peer. Data that is not provided will be generated automatically (Even the ip address!).
                    Generation of some fields (For example, Preshared key) can be disabled by sending an empty value.
                    Available parameters:
                    - publicKey - Public key of the peer (Will be generated if not provided)
                    - presharedKey - Preshared key or empty if no psk required (Will be generated if not provided)
                    - privateKey - Private key of the peer (Will be generated if not provided)
                    - allowedIps - Ip of new peer in wireguard network interface, or empty if no address is required (Will be generated if not provided). Example: 10.0.0.11/32
                    - persistentKeepalive - Persistent keepalive interval in seconds (0 is default)
                    """,
            tags = {"Peers"},
            security = @SecurityRequirement(name = "Token"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = CreatedPeerDTO.class),
                                            examples = {
                                                    @ExampleObject(name = "Created peer", ref = "#/components/examples/createdPeer")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class, name = "BadRequestExample"),
                                    examples = {
                                            @ExampleObject(name = "Invalid key",
                                                    ref = "#/components/examples/InvalidPubKey400")
                                    }
                            )}
                    ),
                    @ApiResponse(responseCode = "409", description = "Peer with new public key already exists",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = AppError.class)),
                                            examples = {
                                                    @ExampleObject(name = "Already exists", ref = "#/components/examples/peerAlreadyExists409"),
                                                    @ExampleObject(name = "Ip already used", ref = "#/components/examples/alreadyUsed409")
                                            }
                                    )}),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class),
                                    examples = {
                                            @ExampleObject(name = "No free ip", ref = "#/components/examples/RangeNoFreeIp500")
                                    }
                            )}
                    )
            }
    )
    @PostMapping
    public ResponseEntity<CreatedPeerDTO> createPeer(@RequestBody(required = false)
                                                         @Parameter(description = "Peer creation request", schema = @Schema(implementation = PeerCreationRequestSchema.class,
                                                                 ref = "#/components/schemas/PeerCreationRequestExample") )
            @Valid PeerCreationRequestDTO peerCreationRequestDTO
    ) {
        peerCreationRequestDTO = Objects.requireNonNullElse(peerCreationRequestDTO, new EmptyPeerCreationRequestDTO());
        CreatedPeer createdPeer = wgPeerService.createPeerGenerateNulls(
                Objects.requireNonNull(creationRequestConverter.convert(peerCreationRequestDTO))
        );
        return new ResponseEntity<>(CreatedPeerDTO.from(createdPeer), HttpStatus.CREATED);
    }
}
