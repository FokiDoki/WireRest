package com.wireguard.api.peer;

import com.wireguard.api.AppError;
import com.wireguard.api.BadRequestException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                            array = @ArraySchema(schema = @Schema(implementation = WgPeerDTO.class))
                    )
            }
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/peers")
    public List<WgPeerDTO> getPeers(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "1000") int limit,
            @RequestParam(value = "sort", required = false, defaultValue = "publicKey.asc") String sortKey
    ) throws ParsingException {
        List<WgPeerDTO> peers;
        try {
            Pageable pageable = PageRequest.of(page, limit, getSort(sortKey));
            peers = wgManager.getPeers(pageable);
        } catch (IllegalArgumentException | ParsingException e){
            throw new BadRequestException(e.getMessage());
        }
        return peers;
    }


    public Sort getSort(String sortKey){
        String[] keys = sortKey.split("\\.");
        if (keys.length == 1){
            return Sort.by(sortKey);
        } else {
            Sort.Direction direction = Sort.Direction.fromString(keys[1]);
            return Sort.by(direction, keys[0]);
        }
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
                 content = { @Content(mediaType = "application/json",
                         schema = @Schema(implementation = AppError.class)) }),
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
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @DeleteMapping("/peer/delete")
    public WgPeerDTO deletePeer(String publicKey) throws ParsingException {
        Optional<WgPeerDTO> peer = wgManager.getPeerDTOByPublicKey(publicKey);
        if (peer.isEmpty()){
            throw new ResourceNotFoundException("Peer with public key %s not found".formatted(publicKey));
        }
        wgManager.deletePeer(publicKey);
        return peer.get();
    }


}
