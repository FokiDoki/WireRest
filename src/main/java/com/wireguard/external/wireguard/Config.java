package com.wireguard.external.wireguard;

import com.wireguard.external.network.IpResolver;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.network.Subnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

@Configuration
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    WgTool wgTool;

    @Autowired
    public Config(WgTool wgTool) {
        this.wgTool = wgTool;
    }


    @Bean
    public IpResolver ipResolver(NetworkInterfaceDTO wgInterface) {

        logger.info("Configuring IpResolver...");
        Subnet interfaceSubnet = wgInterface.getCidrV4Address().stream().findFirst().orElseThrow(
                () -> new RuntimeException("Interface " + wgInterface.getName() + " has no IPv4 address")
        );
        logger.info("Using interface: %s, address: %s, mask: %d".formatted(
                wgInterface.getName(),
                interfaceSubnet.toString(),
                interfaceSubnet.getNumericMask()
                ));
        IpResolver ipResolver = new IpResolver(interfaceSubnet);

        if (interfaceSubnet.getNumericMask()<=30) {
            ipResolver.takeIp(interfaceSubnet.getFirstIpString());
            ipResolver.takeIp(interfaceSubnet.getLastIpString());
            wgInterface.getIpv4Addresses().forEach(iNet4Address -> ipResolver.takeIp(iNet4Address.getHostAddress()));
        }
        consumeUsedIps(ipResolver::takeIp, wgInterface.getName());

        logger.info("IpResolver configured, available IPs: %d, used IPs: %d, Total: %d".formatted(
                ipResolver.getAvailableIpsCount(),
                ipResolver.getUsedIpsCount(),
                ipResolver.getTotalIpsCount()-2
        ));
        return ipResolver;
    }



    private void consumeUsedIps(Consumer<String> consumer, String interfaceName) {
        wgTool.showDump(interfaceName).peers().forEach(
                peer -> peer.getAllowedIps().getIPv4IPs().stream()
                        .map(Subnet::toString)
                        .forEach(consumer)
        );
    }


    @Profile("prod")
    @Bean
    public NetworkInterfaceDTO wgInterface(
            @Value("${wg.interface.name}") String interfaceName
    ) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        if (networkInterface == null) {
            logger.error("Network interface {} not found", interfaceName);
            throw new RuntimeException("Network interface not found");
        }
        NetworkInterfaceDTO networkInterfaceDTO = new NetworkInterfaceDTO(interfaceName);
        networkInterface.getInterfaceAddresses().stream()
                        .filter(interfaceAddress -> interfaceAddress.getAddress() instanceof Inet4Address)
                        .findFirst().ifPresentOrElse( networkInterfaceDTO::addInterfaceAddress,
                            () -> {
                                logger.error("Network interface {} has no IPv4 address", interfaceName);
                                throw new RuntimeException("Network interface %s has no IPv4 address".formatted(interfaceName));
                            });
        networkInterface.getInetAddresses().asIterator().forEachRemaining(
                inetAddress -> {
                    if (inetAddress instanceof Inet4Address) networkInterfaceDTO.addAddress((Inet4Address) inetAddress);
                });
        return networkInterfaceDTO;
    }

    @Profile("test")
    @Bean
    public NetworkInterfaceDTO wgInterfaceTest(
            @Value("${wg.interface.name}") String interfaceName,
            @Value("${wg.test.interface.cidr}") String cidr,
            @Value("${wg.test.interface.interfaceIp}") String interfaceIp
    ) throws UnknownHostException {
        NetworkInterfaceDTO networkInterfaceDTO = new NetworkInterfaceDTO(interfaceName);
        Subnet subnet = Subnet.valueOf(cidr);
        networkInterfaceDTO.addInterfaceAddress(subnet);
        networkInterfaceDTO.addAddress((Inet4Address) Inet4Address.getByName(interfaceIp));
        return networkInterfaceDTO;
    }
}
