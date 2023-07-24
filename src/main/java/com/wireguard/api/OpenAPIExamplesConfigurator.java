package com.wireguard.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.examples.Example;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

@Configuration
public class OpenAPIExamplesConfigurator {
    private final ExamplesCustomizer examplesCustomizer;

    @Autowired
    public OpenAPIExamplesConfigurator(ExamplesCustomizer examplesCustomizer) {
        this.examplesCustomizer = examplesCustomizer;
    }


    @Bean
    public OpenApiCustomizer openApiExamplesCustomizer(ExamplesCustomizer examplesCustomizer) {
        return openApi -> {
            openApi.getComponents().setExamples(examplesCustomizer.getMap());
        };
    }

    @PostConstruct
    public void invalidPubKey() {
        examplesCustomizer.put(
                "InvalidPubKey400",
                new Example().summary("Invalid public key").value(
                        new AppError(400, "publicKey.value: Invalid key format (Base64 required) (test provided), " +
                                "publicKey.value: Key must be 44 characters long (test provided)")
                )
        );
    }

    @PostConstruct
    public void unexpectedError(){
        examplesCustomizer.put(
                "UnexpectedError500",
                new Example().summary("Unexpected error").value(
                        new AppError(500, "Unexpected error")
                )
        );
    }

    @PostConstruct
    public void rangeNoFreeIp(){
        examplesCustomizer.put(
                "RangeNoFreeIp500",
                new Example().summary("No free ip").value(
                        new AppError(500, "Range 10.0.0.0 - 10.0.0.255 has no free ip that can be assigned")
                )
        );
    }


}
