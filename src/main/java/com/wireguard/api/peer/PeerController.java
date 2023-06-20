package com.wireguard.api.peer;

import com.wireguard.api.AppError;
import com.wireguard.api.ResourceNotFoundException;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.WgManager;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

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
                            array = @ArraySchema(schema = @Schema(implementation = WgPeerDTO.class))
                    )
            }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/peers")
    public Set<WgPeerDTO> getPeers() throws ParsingException {
        return wgManager.getPeers();
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
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/peer")
    public WgPeerDTO getPeerByPublicKey(String publicKey) throws ParsingException {
        Optional<WgPeerDTO> peer =  wgManager.getPeerDTOByPublicKey(publicKey);
        if (peer.isPresent()){
            return peer.get();
        } else {
            throw new ResourceNotFoundException("Peer not found");
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreatedPeer.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @PostMapping("/peer/create")
    public ResponseEntity<CreatedPeer> createPeer() {
        return new ResponseEntity<>(wgManager.createPeer(), HttpStatus.CREATED);
    }



}
