package com.wirerest.api.peer;

import com.wirerest.network.Subnet;
import com.wirerest.network.SubnetV6;
import com.wirerest.wireguard.peer.WgPeer;

import java.util.List;
import java.util.Set;

public class testData {
    public static List<WgPeer> getPeers() {
        return List.of(
                WgPeer.publicKey(getFakePubKey()).build(),
                WgPeer.publicKey("PubKey2")
                        .presharedKey("PresharedKey2")
                        .allowedIPv4Subnets(Set.of(Subnet.valueOf("10.0.0.1/32"), Subnet.valueOf("10.1.1.1/30")))
                        .allowedIPv6Subnets(Set.of(SubnetV6.valueOf("2001:db8::/32")))
                        .transferTx(100)
                        .transferRx(200)
                        .latestHandshake(300)
                        .endpoint("1.1.1.1")
                        .build()
        );
    }

    public static String getFakePubKey() {
        return "CkwTo0AKBXMyX9Mqf0SRrq31hZAb6s5C7k1UU94m024=";
    }
}
