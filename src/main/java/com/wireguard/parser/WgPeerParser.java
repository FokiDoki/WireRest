package com.wireguard.parser;

import com.wireguard.DTO.WgPeer;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WgPeerParser {
    public static WgPeer parse(List<String> args){
        Assert.isTrue(args.size() == 8, "WgPeerParser.parse: invalid number of arguments");
        args = trimAll(args);

        String publicKey = args.get(0);
        String presharedKey = args.get(1);
        InetSocketAddress endpoint = parseEndpoint(args.get(2));
        String allowedIps = args.get(3);
        Timestamp latestHandshake = new Timestamp(Long.parseLong(args.get(4))*1000L);
        long transferRx = Long.parseLong(args.get(5));
        long transferTx = Long.parseLong(args.get(6));
        int persistentKeepaliveEnabled = parsePersistentKeepalive(args.get(7));

        return new WgPeer(publicKey, presharedKey, endpoint,
                allowedIps, latestHandshake, transferRx,
                transferTx, persistentKeepaliveEnabled);
    }

    private static ArrayList<String> trimAll(List<String> args){
        ArrayList<String> trimmedArgs = new ArrayList<>();
        for (String arg : args) {
            trimmedArgs.add(arg.trim());
        }
        return trimmedArgs;
    }

    private static int parsePersistentKeepalive(String persistentKeepalive){
        if (persistentKeepalive.equals("off")) return 0;
        return Integer.parseInt(persistentKeepalive);
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
}
