package com.wireguard.external.wireguard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
}
