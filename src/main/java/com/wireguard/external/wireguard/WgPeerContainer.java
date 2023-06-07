package com.wireguard.external.wireguard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class WgPeerContainer {
    private List<WgPeer> peers;

    public WgPeerContainer(){
        peers = new ArrayList<>();
    }
    public void addPeer(WgPeer peer){
        peers.add(peer);
    }

    public void removePeer(WgPeer peer){
        peers.remove(peer);
    }

    public void removePeer(int index){
        peers.remove(index);
    }

    public void clearPeers(){
        peers.clear();
    }

    public WgPeer getPeer(int index){
        return peers.get(index);
    }

    public int size(){
        return peers.size();
    }

    public WgPeer getByPublicKey(String publicKey){
        for(WgPeer peer : peers){
            if(peer.getPublicKey().equals(publicKey)){
                return peer;
            }
        }
        return null;
    }

    public WgPeer getByPresharedKey(String presharedKey){
        for(WgPeer peer : peers){
            if(peer.getPresharedKey().equals(presharedKey)){
                return peer;
            }
        }
        return null;
    }

    public Set<String> getIpv4Addresses(){
        Set<String> ipv4Addresses = new HashSet<>();
        for(WgPeer peer : peers){
            ipv4Addresses.addAll(peer.getAllowedIps().getIPv4IPs());
        }
        return ipv4Addresses;
    }
}
