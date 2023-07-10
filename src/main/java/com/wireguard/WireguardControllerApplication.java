package com.wireguard;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.wireguard.logs.LogbackHandler;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class WireguardControllerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WireguardControllerApplication.class, args);
    }
}
