package com.wireguard.parser;

import com.wireguard.external.network.Subnet;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.utils.IpUtils;
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

        Map<IpUtils.IpType, Set<String>> allowedIps = IpUtils.splitIpv4AndIpv6(splitToStringSet(WgShowPeerDump.get(3), ","));
        return WgPeer.
                publicKey( WgShowPeerDump.get(0))
                .presharedKey(WgShowPeerDump.get(1))
                .endpoint(WgShowPeerDump.get(2))
                .allowedIPv4Subnets(allowedIps.get(IpUtils.IpType.IPV4).stream().map(Subnet::valueOf).collect(Collectors.toSet()))
                .allowedIPv6Subnets(allowedIps.get(IpUtils.IpType.IPV6))
                .latestHandshake(Long.parseLong(WgShowPeerDump.get(4)))
                .transferRx(Long.parseLong(WgShowPeerDump.get(5)))
                .transferTx(Long.parseLong(WgShowPeerDump.get(6)))
                .persistentKeepalive(parsePersistentKeepalive(WgShowPeerDump.get(7)))
                .build();
    }

    private static Set<String> splitToStringSet(String string, String splitter){
        return Set.of(string.split(splitter));
    }

    private static int parsePersistentKeepalive(String persistentKeepalive){
        if (persistentKeepalive.equals("off")) return 0;
        return Integer.parseInt(persistentKeepalive);
    }



}
