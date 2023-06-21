package com.wireguard.external.wireguard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

@Configuration
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    @Value("${wg.interface.subnet}")
    private String interfaceSubnetString;

    @Bean
    public IpResolver ipResolver(
            WgTool wgTool,
            WgInterface wgInterface
    ) throws IOException {
        logger.info("Configuring IpResolver...");
        Subnet interfaceSubnet = getInterfaceSubnet();
        IpResolver ipResolver = new IpResolver(interfaceSubnet);
        ipResolver.takeIp(interfaceSubnet.getFirstIpString());
        ipResolver.takeIp(interfaceSubnet.getLastIpString());
        ipResolver.takeIp(wgInterface.ip());

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
            @Value("${wg.interface.name}") String interfaceName
    ) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            System.out.println(networkInterface.getName());
            System.out.println(networkInterface.getDisplayName());
            System.out.println(networkInterface.getIndex());
            System.out.println(networkInterface.getMTU());
            System.out.println(networkInterface.isLoopback());
            System.out.println(networkInterface.isPointToPoint());
            System.out.println(networkInterface.isUp());
            System.out.println(networkInterface.isVirtual());
            System.out.println(networkInterface.supportsMulticast());
            System.out.println(networkInterface.getHardwareAddress());
            System.out.println(networkInterface.getInterfaceAddresses());
            System.out.println(networkInterface.getSubInterfaces());
            System.out.println(networkInterface.getParent());
            System.out.println(networkInterface.getNetworkInterfaces());
            System.out.println(networkInterface.getSubInterfaces());
            System.out.println(networkInterface.getInterfaceAddresses());
            System.out.println("\n\n");
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            // ...
        }
        logger.info("Configuring WgInterface...");
        return new WgInterface(interfaceName, "10.11.1.1", getInterfaceSubnet());
    }





    private Subnet getInterfaceSubnet(){
        return Subnet.fromString(interfaceSubnetString);
    }
}
