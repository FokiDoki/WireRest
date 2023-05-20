package com.wireguard.external.wireguard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
@AllArgsConstructor
public class WgPeer {
    private String publicKey;
    public String presharedKey;
    public String endpoint;
    public String allowedIps;
    public long latestHandshake;
    public long transferRx;
    public long transferTx;
    public int persistentKeepalive;


}