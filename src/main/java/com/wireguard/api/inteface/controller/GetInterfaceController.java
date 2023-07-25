package com.wireguard.api.inteface.controller;

import com.wireguard.api.inteface.WgInterfaceDTO;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.iface.WgInterfaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetInterfaceController {

    WgInterfaceService interfaceService;

    @Autowired
    public GetInterfaceController(WgInterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @Operation(summary = "Get interface configuration",
            description = "If caching is enabled, the interface configuration is updated every 300 seconds",
            tags = {"Interface"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = WgInterfaceDTO.class),
                                    examples = {
                                            @ExampleObject(name = "Interface",
                                                    ref = "#/components/examples/interface")
                                    })}),
            })
    @GetMapping("v1/interface")
    public WgInterfaceDTO getInterface() throws ParsingException {
        return WgInterfaceDTO.from(interfaceService.getInterface());
    }


}
