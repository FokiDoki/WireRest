package com.wirerest.api.peer.controller;

import com.wirerest.api.AppError;
import com.wirerest.api.converters.PageDTOFromPageTypeChangeConverter;
import com.wirerest.api.converters.PageRequestFromDTOConverter;
import com.wirerest.api.converters.WgPeerDTOFromWgPeerConverter;
import com.wirerest.api.dto.PageDTO;
import com.wirerest.api.dto.PageRequestDTO;
import com.wirerest.api.peer.WgPeerDTO;
import com.wirerest.wireguard.ParsingException;
import com.wirerest.wireguard.peer.WgPeer;
import com.wirerest.wireguard.peer.WgPeerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/peers")
@Validated
public class GetPeersController {

    private final WgPeerService wgPeerService;

    private final WgPeerDTOFromWgPeerConverter peerDTOConverter = new WgPeerDTOFromWgPeerConverter();
    private final PageRequestFromDTOConverter pageRequestConverter = new PageRequestFromDTOConverter();
    private final PageDTOFromPageTypeChangeConverter<WgPeer, WgPeerDTO> pageDTOConverter = new PageDTOFromPageTypeChangeConverter<>(peerDTOConverter);

    public GetPeersController(WgPeerService wgPeerService) {
        this.wgPeerService = wgPeerService;
    }

    @Operation(summary = "List of peers",
            description = "Get a list of all existing peers. Peers are displayed not completely, but page by page." +
                    " Warning, caching is enabled by default, any changes made NOT with wirerest will not appear " +
                    "immediately, but during the next synchronization (60s by default)",
            tags = {"Peers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(nullable = true, implementation = PageDTO.class)),
                                            examples = {
                                                    @ExampleObject(name = "Page", ref = "#/components/examples/PageWithPeers")
                                            }
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class, name = "BadRequestExample"),
                                    examples = {
                                            @ExampleObject(name = "Invalid key format",
                                                    ref = "#/components/examples/InvalidPubKey400"),
                                            @ExampleObject(name = "Invalid page",
                                                    ref = "#/components/examples/InvalidPage400"),
                                    }
                            )}
                    )
            }
    )
    @GetMapping
    @Parameter(name = "page", description = "Page number")
    @Parameter(name = "limit", description = "Page size (In case of 0, all peers will be returned)", schema = @Schema(defaultValue = "100"))
    @Parameter(name = "sort", description = "Sort key and direction separated by a dot. The keys are the same as in the answer. " +
            "Direction is optional and may have value DESC (High to low) and ASC (Low to high). Using with a large number " +
            "of the peers (3000 or more) affects performance. Example: \"lastHandshakeTime.DESC\"")
    @Parameter(name = "pageRequestDTO", hidden = true)
    public PageDTO<WgPeerDTO> getPeers(
            @Valid PageRequestDTO pageRequestDTO
    ) throws ParsingException {
        Page<WgPeer> peers;
        Pageable pageable = pageRequestConverter.convert(pageRequestDTO);
        peers = wgPeerService.getPeers(pageable);
        return pageDTOConverter.convert(peers);
    }
}
