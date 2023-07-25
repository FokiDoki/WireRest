package com.wireguard.api.peer.controller;

import com.wireguard.api.AppError;
import com.wireguard.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wireguard.api.dto.PublicKeyDTO;
import com.wireguard.api.peer.WgPeerDTO;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/peers")
@Validated
public class FindPeerController {

    private final WgPeerService wgPeerService;


    private final WgPeerDTOFromWgPeerConverter peerDTOConverter = new WgPeerDTOFromWgPeerConverter();

    public FindPeerController(WgPeerService wgPeerService) {
        this.wgPeerService = wgPeerService;
    }


    @Operation(summary = "Find by public key",
            description = "",
            tags = {"Peers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WgPeerDTO.class),
                                            examples = {
                                                    @ExampleObject(name = "Peer",
                                                            ref = "#/components/examples/peer")
                                            }

                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class),
                                    examples = {
                                            @ExampleObject(name = "Invalid key format",
                                                    ref = "#/components/examples/peerNotFound")
                                    })})
            })
    @Parameter(name = "publicKey", description = "The public key of the peer to be found", required = true)
    @Parameter(name = "publicKeyDTO", hidden = true)
    @GetMapping("/find")
    public WgPeerDTO getPeerByPublicKey(
            @Valid PublicKeyDTO publicKeyDTO) {
        WgPeer peer = wgPeerService.getPeerByPublicKeyOrThrow(publicKeyDTO.getValue());
        return peerDTOConverter.convert(peer);
    }


}
