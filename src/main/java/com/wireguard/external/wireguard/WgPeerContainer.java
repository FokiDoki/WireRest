package com.wireguard.external.wireguard;

import com.wireguard.external.wireguard.dto.WgPeerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

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

    public Optional<WgPeer> getByPublicKey(String publicKey){
        for(WgPeer peer : peers){
            if(peer.getPublicKey().equals(publicKey)){
                return Optional.of(peer);
            }
        }
        return Optional.empty();
    }

    public Optional<WgPeer> getByPresharedKey(String presharedKey){
        for(WgPeer peer : peers){
            if(peer.getPresharedKey().equals(presharedKey)){
                return Optional.of(peer);
            }
        }
        return Optional.empty();
    }

    public Optional<WgPeerDTO> getDTOByPublicKey(String publicKey){
        Optional<WgPeer> peer = getByPublicKey(publicKey);
        return peer.map(WgPeerDTO::from);
    }

    public Optional<WgPeerDTO> getDTOByPresharedKey(String presharedKey){
        Optional<WgPeer> peer = getByPresharedKey(presharedKey);
        return peer.map(WgPeerDTO::from);
    }

    public Set<String> getIpv4Addresses(){
        return peers.stream()
                .map(peer -> peer.getAllowedIps().getIPv4IPs())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<WgPeer> toSet(){
        return new HashSet<>(peers);
    }

    public Set<WgPeerDTO> toDTOSet(){
        return peers.stream()
                .map(WgPeerDTO::from)
                .collect(Collectors.toSet());
    }
}
