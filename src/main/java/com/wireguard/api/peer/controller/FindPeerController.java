package com.wireguard.api.peer.controller;

import com.wireguard.api.AppError;
import com.wireguard.api.converters.*;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.api.dto.PageRequestDTO;
import com.wireguard.api.dto.PublicKeyDTO;
import com.wireguard.api.peer.CreatedPeerDTO;
import com.wireguard.api.peer.PeerCreationRequestDTO;
import com.wireguard.api.peer.WgPeerDTO;
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
public class FindPeerController {

    private final WgPeerService wgPeerService;



    private final WgPeerDTOFromWgPeerConverter peerDTOConverter = new WgPeerDTOFromWgPeerConverter();

    public FindPeerController(WgPeerService wgPeerService) {
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
