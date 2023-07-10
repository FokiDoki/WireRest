package com.wireguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class WireguardControllerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WireguardControllerApplication.class, args);
    }
}
