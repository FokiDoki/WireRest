package com.wireguard.api.inteface;

import com.wireguard.api.AppError;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.iface.WgInterface;
import com.wireguard.external.wireguard.iface.WgInterfaceService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterfaceController {

    WgInterfaceService interfaceService;

    @Autowired
    public InterfaceController(WgInterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WgInterfaceDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class)) }) })
    @GetMapping("/interface")
    public WgInterfaceDTO getInterface() throws ParsingException {
        return WgInterfaceDTO.from(interfaceService.getInterface());
    }


}
