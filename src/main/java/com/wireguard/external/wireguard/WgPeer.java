package com.wireguard.external.wireguard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;
import java.time.Instant;

@Data
@AllArgsConstructor
public class WgPeer {
    private String publicKey;
    public String presharedKey;
    public InetSocketAddress endpoint;
    public String allowedIps;
    public Instant latestHandshake;
    public long transferRx;
    public long transferTx;
    public int persistentKeepalive;


}