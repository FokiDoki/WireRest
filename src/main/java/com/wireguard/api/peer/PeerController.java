package com.wireguard.api.peer;

import com.wireguard.api.AppError;
import com.wireguard.api.BadRequestException;
import com.wireguard.api.ResourceNotFoundException;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.peer.CreatedPeer;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PeerController {

    WgPeerService wgPeerService;

    public PeerController(WgPeerService wgPeerService) {
        this.wgPeerService = wgPeerService;
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
    @Parameter(name = "page", description = "Page number")
    @Parameter(name = "limit", description = "Page size (In case of 0, all peers will be returned)")
    @Parameter(name = "sort", description = "Sort key and direction separated by a dot. The keys are the same as in the answer" +
            "Direction is optional and may have value DESC (High to low) and ASC (Low to high). Default is allowedSubnets.DESC", example = "transferTx.desc")
    public PageDTO<WgPeerDTO> getPeers(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "1000") int limit,
            @RequestParam(value = "sort", required = false, defaultValue = "allowedSubnets.asc") String sortKey
    ) throws ParsingException {
        Page<WgPeer> peers;
        if (limit == 0){
            limit = Integer.MAX_VALUE;
        }
        try {
            Pageable pageable = PageRequest.of(page, limit, getSort(sortKey));
            peers = wgPeerService.getPeers(pageable);

        } catch (IllegalArgumentException | ParsingException e){
            throw new BadRequestException(e.getMessage());
        }
        return pagePeerToPageDTOPeerDTO(peers);
    }

    private PageDTO<WgPeerDTO> pagePeerToPageDTOPeerDTO(Page<WgPeer> peers){
        List<WgPeerDTO> peerDTOs = peers.getContent().stream().map(WgPeerDTO::from).collect(Collectors.toList());
        return new PageDTO<>(peers.getTotalPages(), peers.getNumber(), peers.getSize(), peerDTOs);
    }


    private Sort getSort(String sortKey){
        String[] keys = sortKey.split("\\.");
        if (keys.length == 1){
            return Sort.by(sortKey).descending();
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
        Optional<WgPeer> peer =  wgPeerService.getPeerByPublicKey(publicKey);
        if (peer.isPresent()){
            return WgPeerDTO.from(peer.get());
        } else {
            throw new ResourceNotFoundException("Peer not found");
        }
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
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request (Invalid parameters values)",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) })})
    @PostMapping("/peer/create")
    @Parameter(name = "publicKey", description = "Public key of the peer (Will be generated if not provided)")
    @Parameter(name = "presharedKey", description = "Preshared key or empty if no psk required (Will be generated if not provided)", allowEmptyValue = true)
    @Parameter(name = "privateKey", description = "Private key of the peer " +
            "(Will be generated if not provided. " +
            "If provided public key, empty string will be returned)")
        @Parameter(name = "address", description = "CIDR of new peer in wireguard network interface, or empty if no address is required (Will be generated if not provided)", schema = @Schema(format = "CIDR"), allowEmptyValue = true)
    @Parameter(name = "persistentKeepalive", description = "Persistent keepalive interval in seconds (0 if not provided)")
    public ResponseEntity<CreatedPeerDTO> createPeer(
            @RequestParam(value = "publicKey", required = false ) String publicKey,
            @RequestParam(value = "presharedKey", required = false ) String presharedKey,
            @RequestParam(value = "privateKey", required = false ) String privateKey,
            @RequestParam(value = "address", required = false) Set<Subnet> address,
            @RequestParam(value = "persistentKeepalive", required = false ) Integer persistentKeepalive
    ) {
        CreatedPeer createdPeer;
        try {
            createdPeer = wgPeerService.createPeerGenerateNulls(
                    publicKey,
                    presharedKey,
                    privateKey,
                    address,
                    persistentKeepalive
            );
        } catch (IllegalArgumentException | ParsingException e){
            throw new BadRequestException(e.getMessage());
        } catch (CommandExecutionException e){
            throw new BadRequestException("Wireguard error: %s".formatted(e.getStderr().strip()));
        }
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
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @DeleteMapping("/peer/delete")
    public WgPeerDTO deletePeer(String publicKey) throws ParsingException {
        Optional<WgPeer> peer = wgPeerService.getPeerByPublicKey(publicKey);
        if (peer.isEmpty()){
            throw new ResourceNotFoundException("Peer with public key %s not found".formatted(publicKey));
        }
        wgPeerService.deletePeer(publicKey);
        return WgPeerDTO.from(peer.get());
    }



}
