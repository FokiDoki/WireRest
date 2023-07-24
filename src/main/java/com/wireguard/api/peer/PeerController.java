package com.wireguard.api.peer;

import com.wireguard.api.AppError;
import com.wireguard.api.converters.*;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.dto.PageRequestDTO;
import com.wireguard.api.dto.PublicKeyDTO;
import com.wireguard.external.wireguard.ParsingException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value = "v1/peers")
@Validated
public class PeerController {

    private final WgPeerService wgPeerService;

    private final PeerCreationRequestFromDTOConverter creationRequestConverter = new PeerCreationRequestFromDTOConverter();


    private final WgPeerDTOFromWgPeerConverter peerDTOConverter = new WgPeerDTOFromWgPeerConverter();

    public PeerController(WgPeerService wgPeerService) {
        this.wgPeerService = wgPeerService;
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WgPeerDTO.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))})})
    @Parameter(name = "publicKey", description = "The public key of the peer to be found", required = true)
    @Parameter(name = "publicKeyDTO", hidden = true)
    @GetMapping("/find")
    public WgPeerDTO getPeerByPublicKey(
            @Valid PublicKeyDTO publicKeyDTO) {
        WgPeer peer = wgPeerService.getPeerByPublicKeyOrThrow(publicKeyDTO.getValue());
        return peerDTOConverter.convert(peer);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreatedPeerDTO.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request (Invalid parameters values)",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))})})
    @PostMapping()
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


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WgPeerDTO.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))})})


    @Parameter(name = "publicKey", description = "The public key of the peer to be deleted", required = true)
    @DeleteMapping()
    public WgPeerDTO deletePeer(@Valid PublicKeyDTO publicKeyDTO) {
        WgPeer deletedPeer = wgPeerService.deletePeer(publicKeyDTO.getValue());
        return peerDTOConverter.convert(deletedPeer);
    }


}
