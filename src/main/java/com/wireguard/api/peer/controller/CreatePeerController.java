package com.wireguard.api.peer.controller;

import com.wireguard.api.AppError;
import com.wireguard.api.converters.PageDTOFromPageTypeChangeConverter;
import com.wireguard.api.converters.PageRequestFromDTOConverter;
import com.wireguard.api.converters.PeerCreationRequestFromDTOConverter;
import com.wireguard.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.peer.CreatedPeerDTO;
import com.wireguard.api.peer.PeerCreationRequestDTO;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.external.wireguard.peer.CreatedPeer;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
            description = "Create peer. Data that is not provided will be generated automatically (Even the ip address!)." +
                    "Generation of some fields (For example, Preshared key) can be disabled by sending an empty value.",
            tags = {"Peers"},
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
                                            @ExampleObject(name = "Invalid key format",
                                                    ref = "#/components/examples/InvalidPubKey400")
                                    }
                            )}
                    ),
                    @ApiResponse(responseCode = "409", description = "Peer with new public key already exists",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = WgPeerDTO.class)),
                                            examples = {
                                                    @ExampleObject(name = "Peer exists", ref = "#/components/examples/peerAlreadyExists409")
                                            }
                                    )}),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class),
                                    examples = {
                                            @ExampleObject(name="No free ip", ref = "#/components/examples/RangeNoFreeIp500"),
                                            @ExampleObject(name="Other errors", ref = "#/components/examples/UnexpectedError500")
                                    }
                            )}
                    )
            }
    )
    @PostMapping
    @Parameter(name = "publicKey", description = "Public key of the peer (Will be generated if not provided)")
    @Parameter(name = "presharedKey", description = "Preshared key or empty if no psk required (Will be generated if not provided)", allowEmptyValue = true)
    @Parameter(name = "privateKey", description = "Private key of the peer " +
            "(Will be generated if not provided. " +
            "If provided public key, empty string will be returned)")
    @Parameter(name = "allowedIps", description = "Ip of new peer in wireguard network interface, or empty if no" +
            " address is required (Will be generated if not provided). Example: 10.0.0.11/32", array = @ArraySchema(arraySchema = @Schema(implementation = String.class), uniqueItems = true), allowEmptyValue = true)
    @Parameter(name = "persistentKeepalive", description = "Persistent keepalive interval in seconds (0 if not provided)", schema = @Schema(implementation = Integer.class, defaultValue = "0", example = "0", minimum = "0", maximum = "65535"))
    @Parameter(name = "peerCreationRequestDTO", hidden = true)
    public ResponseEntity<CreatedPeerDTO> createPeer(
            @Valid PeerCreationRequestDTO peerCreationRequestDTO
    ) {
        CreatedPeer createdPeer = wgPeerService.createPeerGenerateNulls(
                Objects.requireNonNull(creationRequestConverter.convert(peerCreationRequestDTO))
        );
        return new ResponseEntity<>(CreatedPeerDTO.from(createdPeer), HttpStatus.CREATED);
    }
}
