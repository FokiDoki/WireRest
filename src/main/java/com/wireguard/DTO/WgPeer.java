package com.wireguard.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class WgPeer {
    private String publicKey;
    public String presharedKey;
    public InetSocketAddress endpoint;
    public String allowedIps;
    public Timestamp latestHandshake;
    public long transferRx;
    public long transferTx;
    public int persistentKeepalive;


}