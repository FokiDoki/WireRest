package com.wireguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.Locale;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WireguardControllerApplication {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(WireguardControllerApplication.class, args);
    }
}
