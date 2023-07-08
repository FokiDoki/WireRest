package com.wireguard.parser;

import com.wireguard.external.wireguard.WgPeer;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        WgShowPeerDump = WgShowPeerDump.stream()
                .map(s -> s.equals("(none)") ? null : s).collect(Collectors.toList());

        Map<IpType, Set<String>> allowedIps = filterAllowedIps(WgShowPeerDump.get(3));
        return WgPeer.
                publicKey( WgShowPeerDump.get(0))
                .presharedKey(WgShowPeerDump.get(1))
                .endpoint(WgShowPeerDump.get(2))
                .allowedIPv4Ips(allowedIps.get(IpType.IPV4))
                .allowedIPv6Ips(allowedIps.get(IpType.IPV6))
                .latestHandshake(Long.parseLong(WgShowPeerDump.get(4)))
                .transferRx(Long.parseLong(WgShowPeerDump.get(5)))
                .transferTx(Long.parseLong(WgShowPeerDump.get(6)))
                .persistentKeepalive(parsePersistentKeepalive(WgShowPeerDump.get(7)))
                .build();
    }

    private enum IpType {
        IPV4, IPV6
    }
    private static Map<IpType, Set<String>> filterAllowedIps(String allowedIps){
        Map<IpType, Set<String>> allowedIpsMap = Map.of(IpType.IPV4, new HashSet<>(), IpType.IPV6, new HashSet<>());
        if (allowedIps==null) return allowedIpsMap;
        Set<String> allowedIpsStringsList = Set.of(allowedIps.split(","));
        allowedIpsStringsList.forEach(allowedIp -> {
            if (Utils.isIpV4Cidr(allowedIp)){
                allowedIpsMap.get(IpType.IPV4).add(allowedIp);
            } else if (Utils.isIpV6Cidr(allowedIp)){
                allowedIpsMap.get(IpType.IPV6).add(allowedIp);
            } else {
                throw new IllegalArgumentException("WgPeerParser.parseAllowedIps: invalid IP address %s".formatted(allowedIp));
            }
        });
        return allowedIpsMap;
    }

    private static int parsePersistentKeepalive(String persistentKeepalive){
        if (persistentKeepalive.equals("off")) return 0;
        return Integer.parseInt(persistentKeepalive);
    }



}
