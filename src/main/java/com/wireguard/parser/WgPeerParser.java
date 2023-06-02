package com.wireguard.parser;

import com.wireguard.external.wireguard.WgPeer;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

public class WgPeerParser {
   public static WgPeer parse(String wgShowPeerDump, String splitter){
        String[] splitWgShowPeerDump = wgShowPeerDump.split(splitter);
        return parse(List.of(splitWgShowPeerDump));
    }


    public static WgPeer parse(List<String> wgShowPeerDumpSource){
        Assert.isTrue(wgShowPeerDumpSource.size() == 8,
                "WgPeerParser.parse: invalid number of arguments in %s, 8 expected"
                        .formatted(wgShowPeerDumpSource.toString())
        );
        List<String> WgShowPeerDump = Utils.trimAll(wgShowPeerDumpSource);


        return WgPeer.
                withPublicKey( WgShowPeerDump.get(0))
                .presharedKey(WgShowPeerDump.get(1))
                .endpoint(parseEndpoint(WgShowPeerDump.get(2)))
                .allowedIps(parseAllowedIps(WgShowPeerDump.get(3)))
                .latestHandshake(Long.parseLong(WgShowPeerDump.get(4)))
                .transferRx(Long.parseLong(WgShowPeerDump.get(5)))
                .transferTx(Long.parseLong(WgShowPeerDump.get(6)))
                .persistentKeepalive(parsePersistentKeepalive(WgShowPeerDump.get(7)))
                .build();
    }


    private static String  parseEndpoint(String endpoint){
        if (endpoint.equals("(none)")) return null;
        return endpoint;
    }

    private static WgPeer.AllowedIps parseAllowedIps(String allowedIps){
        Set<String> allowedIpsList = Set.of(allowedIps.split(","));
        WgPeer.AllowedIps allowedIpsObject = new WgPeer.AllowedIps();
        for (String allowedIp : allowedIpsList){

            if (Utils.isIpV4Cidr(allowedIp)){
                allowedIpsObject.addIpv4(allowedIp);
            } else if (Utils.isIpV6Cidr(allowedIp)){
                allowedIpsObject.addIpv6(allowedIp);
            } else {
                throw new IllegalArgumentException("WgPeerParser.parseAllowedIps: invalid IP address %s".formatted(allowedIp));
            }
        }
        return allowedIpsObject;
    }

    private static int parsePersistentKeepalive(String persistentKeepalive){
        if (persistentKeepalive.equals("off")) return 0;
        return Integer.parseInt(persistentKeepalive);
    }



}
