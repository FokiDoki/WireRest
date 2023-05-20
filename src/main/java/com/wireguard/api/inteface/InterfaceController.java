package com.wireguard.api.inteface;

import com.wireguard.api.AppError;
import com.wireguard.external.wireguard.BadInterfaceException;
import com.wireguard.external.wireguard.WgManager;
import com.wireguard.external.wireguard.dto.WgInterface;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterfaceController {

    WgManager wgManager;

    @Autowired
    public InterfaceController(WgManager wgManager) {
        this.wgManager = wgManager;
    }



    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WgInterface.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/interface")
    public WgInterface getInterface() throws BadInterfaceException {
        return wgManager.getInterface();
    }
}
