package com.wireguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WireguardControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WireguardControllerApplication.class, args);
        System.out.println("some dangerous code");
    }

}
