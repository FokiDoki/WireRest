package com.wirerest.wireguard.parser;

import com.wirerest.network.ISubnet;
import com.wirerest.utils.IpUtils;
import com.wirerest.wireguard.peer.WgPeer;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WgPeerParser {
    public static WgPeer parse(String wgShowPeerDump, String splitter) {
        String[] splitWgShowPeerDump = wgShowPeerDump.split(splitter);
        return parse(List.of(splitWgShowPeerDump));
    }


    public static WgPeer parse(List<String> wgShowPeerDumpSource) {
        Assert.isTrue(wgShowPeerDumpSource.size() == 8,
                "WgPeerParser.parse: invalid number of arguments in %s, 8 expected"
                        .formatted(wgShowPeerDumpSource.toString())
        );
        List<String> WgShowPeerDump = Utils.trimAll(wgShowPeerDumpSource);
        WgShowPeerDump = WgShowPeerDump.stream()
                .map(s -> s.equals("(none)") ? null : s).collect(Collectors.toList());

        Set<ISubnet> allowedIps = IpUtils.stringToSubnetSet(splitToStringSet(WgShowPeerDump.get(3), ","));
        return WgPeer.
                publicKey(WgShowPeerDump.get(0))
                .presharedKey(WgShowPeerDump.get(1))
                .endpoint(WgShowPeerDump.get(2))
                .allowedIps(allowedIps)
                .latestHandshake(Long.parseLong(WgShowPeerDump.get(4)))
                .transferRx(Long.parseLong(WgShowPeerDump.get(5)))
                .transferTx(Long.parseLong(WgShowPeerDump.get(6)))
                .persistentKeepalive(parsePersistentKeepalive(WgShowPeerDump.get(7)))
                .build();
    }

    private static Set<String> splitToStringSet(String string, String splitter) {
        if (string == null) return Set.of();
        return Set.of(string.split(splitter));
    }

    private static int parsePersistentKeepalive(String persistentKeepalive) {
        if (persistentKeepalive.equals("off")) return 0;
        return Integer.parseInt(persistentKeepalive);
    }


}
