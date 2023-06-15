package com.wireguard.external.wireguard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    @Value("${wg.interface.subnet}")
    private String interfaceSubnetString;

    @Bean
    public IpResolver ipResolver(
            @Value("${wg.interface.ip}") String interfaceIp,
            WgTool wgTool,
            WgInterface wgInterface
    ) throws IOException {
        logger.info("Configuring IpResolver...");
        Subnet interfaceSubnet = getInterfaceSubnet();
        IpResolver ipResolver = new IpResolver(interfaceSubnet);
        ipResolver.takeIp(interfaceSubnet.getFirstIpString());
        ipResolver.takeIp(interfaceSubnet.getLastIpString());
        ipResolver.takeIp(interfaceIp);

        wgTool.showDump(wgInterface.name()).peers().forEach(
                peer ->
                        peer.getAllowedIps().getIPv4IPs().forEach(
                                allowedIp ->
                                    ipResolver.takeSubnet(Subnet.fromString(allowedIp))
                        )
        );

        return ipResolver;
    }

    @Bean
    public WgInterface wgInterface(
            @Value("${wg.interface.name}") String interfaceName,
            @Value("${wg.interface.ip}") String interfaceIp
    ){
        logger.info("Configuring WgInterface...");
        return new WgInterface(interfaceName, interfaceIp);
    }





    private Subnet getInterfaceSubnet(){
        return Subnet.fromString(interfaceSubnetString);
    }
}
