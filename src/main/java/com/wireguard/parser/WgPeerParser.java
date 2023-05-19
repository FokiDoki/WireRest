package com.wireguard.parser;

import com.wireguard.external.wireguard.dto.WgPeer;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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


        return new WgPeer(
                parseAndValidatePublicKey(WgShowPeerDump.get(0)),
                parseAndValidatePresharedKey(WgShowPeerDump.get(1)),
                parseEndpoint(WgShowPeerDump.get(2)),
                parseAndValidateAllowedIps(WgShowPeerDump.get(3)),
                parseAndValidateLatestHandshake(WgShowPeerDump.get(4)),
                parseAndValidateTransferRx(WgShowPeerDump.get(5)),
                parseAndValidateTransferTx(WgShowPeerDump.get(6)),
                parsePersistentKeepalive(WgShowPeerDump.get(7)));
    }


    private static String parseAndValidatePublicKey(String publicKey){
        return Validator.validateKey(publicKey);
    }

    private static String parseAndValidatePresharedKey(String presharedKey){
        return Validator.validateKey(presharedKey);
    }

    private static InetSocketAddress parseEndpoint(String endpoint){
        if (endpoint.equals("(none)")) return null;
        Assert.doesNotContain(":", endpoint, "WgPeerParser.parseEndpoint: invalid endpoint "+endpoint);
        int endpointPort = Integer.parseInt(endpoint.split(":")[1].trim());
        return new InetSocketAddress(
                endpoint.split(":")[0],
                endpointPort
        );
    }

    private static String parseAndValidateAllowedIps(String allowedIps){
        return allowedIps; //TODO: do validation mechanism
    }

    private static Instant parseAndValidateLatestHandshake(String latestHandshake){
        long latestHandshakeLong = Long.parseLong(latestHandshake);
        return Instant.ofEpochSecond(latestHandshakeLong);
    }

    private static long parseAndValidateTransferRx(String transferRx){
        return Long.parseLong(transferRx);
    }

    private static long parseAndValidateTransferTx(String transferTx){
        return Long.parseLong(transferTx);
    }

    private static int parsePersistentKeepalive(String persistentKeepalive){
        if (persistentKeepalive.equals("off")) return 0;
        return Integer.parseInt(persistentKeepalive);
    }



}
