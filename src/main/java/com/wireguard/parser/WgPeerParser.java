package com.wireguard.parser;

import com.wireguard.external.wireguard.WgPeer;
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


        return WgPeer.
                withPublicKey(parseAndValidatePublicKey( WgShowPeerDump.get(0) ))
                .presharedKey(parseAndValidatePresharedKey(WgShowPeerDump.get(1)))
                .endpoint(parseEndpoint(WgShowPeerDump.get(2)))
                .allowedIps(parseAndValidateAllowedIps(WgShowPeerDump.get(3)))
                .latestHandshake(parseAndValidateLatestHandshake(WgShowPeerDump.get(4)))
                .transferRx(parseAndValidateTransferRx(WgShowPeerDump.get(5)))
                .transferTx(parseAndValidateTransferTx(WgShowPeerDump.get(6)))
                .persistentKeepalive(parsePersistentKeepalive(WgShowPeerDump.get(7)))
                .build();
    }


    private static String parseAndValidatePublicKey(String publicKey){
        return Validator.validateKey(publicKey);
    }

    private static String parseAndValidatePresharedKey(String presharedKey){
        return Validator.validateKey(presharedKey);
    }

    private static String  parseEndpoint(String endpoint){
        if (endpoint.equals("(none)")) return null;
        return endpoint;
    }

    private static String parseAndValidateAllowedIps(String allowedIps){
        return allowedIps; //TODO: do validation mechanism   !
    }

    private static Long parseAndValidateLatestHandshake(String latestHandshake){
        return Long.parseLong(latestHandshake);
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
