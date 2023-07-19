package com.wireguard.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(

        info = @Info(
                title = "WireRest",
                description = "Docs for Wireguard API",
                version = "1",
                contact = @Contact(
                        name = "FokiDoki",
                        url = "https://github.com/FokiDoki/"
                )
        )
)
public class OpenAPIServerConfig {

}
