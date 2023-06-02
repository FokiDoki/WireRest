package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class WgManager {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    @Value("${wg.interface.name}")
    private String interfaceName = "wg0";
    @Value("${wg.interface.new_cient_subnet_mask}")
    private int defaultMask;
    private final IpResolver wgIpResolver;

    private static WgTool wgTool;

    @Autowired
    public WgManager(WgTool wgTool,
                     @Value("${wg.interface.subnet}") String subnet,
                     @Value("${wg.interface.ip}") String interfaceIp) {
        WgManager.wgTool = wgTool;

        Subnet allowedSubnet = Subnet.fromString(subnet);
        wgIpResolver = new IpResolver(allowedSubnet);
        Set<String> forbiddenIps = getBusyIpv4Set();
        forbiddenIps.add(interfaceIp);
        forbiddenIps.add(allowedSubnet.getIpString());
        configureIpResolver(forbiddenIps);
    }

    private void configureIpResolver(Set<String> ipsToExclude){
        for (String ip : ipsToExclude){
            wgIpResolver.takeSubnet(Subnet.fromString(ip+"/32"));
        }
    }



    public WgInterface getInterface()  {
        return getDump().getWgInterface();
    }

    public List<WgPeer> getPeers()  {
        return getDump().getPeers();
    }

    private WgShowDump getDump() {
        try{
            return wgTool.showDump(interfaceName);
        } catch (IOException e) {
            logger.error("Error getting dump", e);
            throw new ParsingException("Error while getting dump", e);
        }
    }

    public Optional<WgPeer> getPeerByPublicKey(String publicKey) throws ParsingException {
        WgPeerContainer peerContainer = getWgPeerContainer();
        WgPeer wgPeer = peerContainer.getByPublicKey(publicKey);
        return Optional.ofNullable(wgPeer);
    }

    private WgPeerContainer getWgPeerContainer()  {
        List<WgPeer> peers = getPeers();
        return new WgPeerContainer(peers);
    }

    public Set<String> getBusyIpv4Set()  {
        WgPeerContainer peerContainer = getWgPeerContainer();
        return peerContainer.getIpv4Addresses();
    }


    public WgPeer createPeer(){
        String privateKey = wgTool.generatePrivateKey().strip();
        String publicKey = wgTool.generatePublicKey(privateKey.strip()).strip();
        String presharedKey = wgTool.generatePresharedKey().strip();
        Subnet address = wgIpResolver.takeFreeSubnet(defaultMask);
        wgTool.addPeer(interfaceName, publicKey, presharedKey, address.toString(), 0);
        return WgPeer.withPublicKey(publicKey)
                .presharedKey(presharedKey)
                .allowedIPv4Ips(Set.of(address.toString()))
                .build();
    }
}
