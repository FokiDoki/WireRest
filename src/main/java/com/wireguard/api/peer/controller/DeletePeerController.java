package com.wireguard.api.peer.controller;

import com.wireguard.api.AppError;
import com.wireguard.api.converters.PeerCreationRequestFromDTOConverter;
import com.wireguard.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.dto.PublicKeyDTO;
import com.wireguard.api.peer.WgPeerDTO;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/peers")
@Validated
public class DeletePeerController {

    private final WgPeerService wgPeerService;

    private final WgPeerDTOFromWgPeerConverter peerDTOConverter = new WgPeerDTOFromWgPeerConverter();

    public DeletePeerController(WgPeerService wgPeerService) {
        this.wgPeerService = wgPeerService;
    }

    @Operation(summary = "Delete peer",
            description = "Delete peer by public key",
            tags = {"Peers"},
            responses = {
                        @ApiResponse(responseCode = "200", description = "OK",
                                content = {
                                        @Content(
                                                mediaType = "application/json",
                                                schema = @Schema(implementation = WgPeerDTO.class),
                                                examples = {
                                                        @ExampleObject(name = "Peer", ref = "#/components/examples/peer")
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
                        @ApiResponse(responseCode = "404", description = "Not Found",
                                content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AppError.class),
                                        examples = {
                                                @ExampleObject(name = "Invalid key format",
                                                        ref = "#/components/examples/peerNotFound")
                                        }

                                )}),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                                content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = AppError.class),
                                        examples = {
                                                @ExampleObject(name = "Other errors", ref = "#/components/examples/UnexpectedError500")
                                        }
                                )}
                        )
            }
    )
    @Parameter(name = "publicKey", description = "The public key of the peer to be deleted", required = true)
    @Parameter(name = "publicKeyDTO", hidden = true)
    @DeleteMapping
    public WgPeerDTO deletePeer(@Valid PublicKeyDTO publicKeyDTO) {
        WgPeer deletedPeer = wgPeerService.deletePeer(publicKeyDTO.getValue());
        return peerDTOConverter.convert(deletedPeer);
    }
}
