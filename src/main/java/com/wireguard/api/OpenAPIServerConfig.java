package com.wireguard.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(

        info = @Info(
                title = "Wireguard API",
                description = "Docs for Wireguard API",
                version = "0.3",
                contact = @Contact(
                        name = "FokiDoki",
                        url = "https://github.com/FokiDoki/"
                )
        )
)
public class OpenAPIServerConfig {

}
