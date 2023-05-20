package com.wireguard.api.peer;

import com.wireguard.api.AppError;

import com.wireguard.api.ResourceNotFoundException;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.WgManager;
import com.wireguard.external.wireguard.WgPeer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class PeerController {

    WgManager wgManager;

    public PeerController(WgManager wgManager) {
        this.wgManager = wgManager;
    }




    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WgPeer.class))
                    )
            }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/peers")
    public List<WgPeer> getPeers() throws ParsingException {
        return wgManager.getPeers();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WgPeer.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/peer/{public_key}")
    public WgPeer getPeerByPublicKey(
            @PathVariable(name = "public_key") String publicKey) throws ParsingException {
        Optional<WgPeer> peer =  wgManager.getPeerByPublicKey(publicKey);
        if (peer.isPresent()){
            return peer.get();
        } else {
            throw new ResourceNotFoundException("Peer not found");
        }
    }






}
