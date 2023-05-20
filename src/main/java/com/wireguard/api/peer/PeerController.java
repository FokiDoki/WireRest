package com.wireguard.api.peer;

import com.wireguard.api.AppError;

import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.WgManager;
import com.wireguard.external.wireguard.WgPeer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PeerController {

    WgManager wgManager;

    public PeerController(WgManager wgManager) {
        this.wgManager = wgManager;
    }



    @GetMapping("/peers")
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
    public List<WgPeer> getPeers() throws ParsingException {
        return wgManager.getPeers();
    }


}
