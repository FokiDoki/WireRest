package com.wireguard.api.peer;

import com.wireguard.api.AppError;
import com.wireguard.api.BadRequestException;
import com.wireguard.api.ResourceNotFoundException;
import com.wireguard.api.dto.PageDTO;
import com.wireguard.external.network.ISubnet;
import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.PeerCreationRequest;
import com.wireguard.external.wireguard.PeerUpdateRequest;
import com.wireguard.external.wireguard.peer.CreatedPeer;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import com.wireguard.utils.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    @Parameter(name = "sort", description = "Sort key and direction separated by a dot. The keys are the same as in the answer. " +
            "Direction is optional and may have value DESC (High to low) and ASC (Low to high). Using with a large number of the peers (3000 or more) affects performance. ", example = "allowedSubnets.desc")
    public PageDTO<WgPeerDTO> getPeers(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "1000") int limit,
            @RequestParam(value = "sort", required = false) String sortKey
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

    @Operation(summary = "Update peer by public key", description = "Update peer by public key. " +
            "Do not provide fields that you do not want to change.")
    @Parameter(name = "publicKey", description = "Current public key of the peer", required = true)
    @Parameter(name = "newPublicKey", description = "New public key of the peer. Warning: If you change the public key, latest handshake and transfer data will be lost. "
            , allowEmptyValue = true)
    @Parameter(name = "presharedKey", description = "Preshared key or empty if no psk required (Empty if not provided)",
            allowEmptyValue = true)
    @Parameter(name = "endpoint", description = "Endpoint IP:port ",
            allowEmptyValue = true)
    @Parameter(name = "allowedIps", description = "New ips of the peer (Exists will be replaced)  Example: 10.0.0.11/32",
            array = @ArraySchema(arraySchema = @Schema(implementation = String.class), uniqueItems=true), allowEmptyValue = true)
    @Parameter(name = "persistentKeepalive", description = "New persistent keepalive interval in seconds (0 if not provided)", schema = @Schema(implementation = Integer.class, defaultValue = "0", example = "0", minimum = "0", maximum = "65535"))
    @Parameter(name = "peerUpdateRequestDTO", hidden = true)
    @RequestMapping(value = "/peer/update", method = RequestMethod.PATCH)
    public WgPeerDTO updatePeer(
        @Valid PeerUpdateRequestDTO peerUpdateRequestDTO
    ){
        WgPeer wgPeer = wgPeerService.updatePeer(
                peerUpdateRequestDTOToPeerUpdateRequest(peerUpdateRequestDTO));
        return WgPeerDTO.from(wgPeer);
    }

    private PeerUpdateRequest peerUpdateRequestDTOToPeerUpdateRequest(PeerUpdateRequestDTO peerUpdateRequestDTO){
        Optional<Set<ISubnet>> allowedIps = Optional.empty();
        if (peerUpdateRequestDTO.getAllowedIps()!=null){
            allowedIps = Optional.of(IpUtils.stringToSubnetSet(peerUpdateRequestDTO.getAllowedIps()));
        }
        return new PeerUpdateRequest(
                peerUpdateRequestDTO.getPublicKey(),
                peerUpdateRequestDTO.getNewPublicKey(),
                peerUpdateRequestDTO.getPresharedKey(),
                allowedIps.orElse(null),
                peerUpdateRequestDTO.getEndpoint(),
                peerUpdateRequestDTO.getPersistentKeepalive()
        );
    }

    private PageDTO<WgPeerDTO> pagePeerToPageDTOPeerDTO(Page<WgPeer> peers){
        List<WgPeerDTO> peerDTOs = peers.getContent().stream().map(WgPeerDTO::from).collect(Collectors.toList());
        return new PageDTO<>(peers.getTotalPages()-1, peers.getNumber(), peerDTOs);
    }


    private Sort getSort(String sortKey){
        if (sortKey == null){
            return Sort.unsorted();
        }
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
    @Parameter(name = "allowedIps", description = "Ip of new peer in wireguard network interface, or empty if no" +
                " address is required (Will be generated if not provided). Example: 10.0.0.11/32", array = @ArraySchema(arraySchema = @Schema(implementation = String.class), uniqueItems=true),allowEmptyValue = true)
    @Parameter(name = "persistentKeepalive", description = "Persistent keepalive interval in seconds (0 if not provided)", schema = @Schema(implementation = Integer.class, defaultValue = "0", example = "0", minimum = "0", maximum = "65535"))
    @Parameter(name = "peerCreationRequestDTO", hidden = true)
    public ResponseEntity<CreatedPeerDTO> createPeer(
            PeerCreationRequestDTO peerCreationRequestDTO,
            @RequestParam(value = "allowedIps", required = false) Set<String> allowedIps
    ) {
        CreatedPeer createdPeer;
        try {
            int countOfIpsToGenerate = allowedIps == null ? 1 : 0;
            createdPeer = wgPeerService.createPeerGenerateNulls(
                    peerCreationRequestDTOToPeerCreationRequest(peerCreationRequestDTO,countOfIpsToGenerate)
            );
        } catch (IllegalArgumentException | ParsingException e){
            throw new BadRequestException(e.getMessage());
        } catch (CommandExecutionException e){
            throw new BadRequestException("Wireguard error: %s".formatted(e.getStderr().strip()));
        }
        return new ResponseEntity<>(CreatedPeerDTO.from(createdPeer), HttpStatus.CREATED);
    }

    private PeerCreationRequest peerCreationRequestDTOToPeerCreationRequest(PeerCreationRequestDTO peerCreationRequestDTO,
                                                                            int countOfIpsToGenerate) throws ParsingException {

        return new PeerCreationRequest(
                peerCreationRequestDTO.getPublicKey(),
                peerCreationRequestDTO.getPresharedKey(),
                peerCreationRequestDTO.getPrivateKey(),
                IpUtils.stringToSubnetSet(
                        Optional.ofNullable(
                                peerCreationRequestDTO.getAllowedIps()
                        ).orElseGet(Set::of)),
                peerCreationRequestDTO.getPersistentKeepalive(),
                countOfIpsToGenerate
        );
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
