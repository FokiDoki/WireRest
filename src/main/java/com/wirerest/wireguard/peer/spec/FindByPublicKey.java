package com.wirerest.wireguard.peer.spec;

import com.wirerest.wireguard.Specification;
import com.wirerest.wireguard.peer.WgPeer;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class FindByPublicKey implements Specification<WgPeer> {

    @Getter
    private final String publicKey;

    public FindByPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean isExist(WgPeer wgPeer) {
        return wgPeer.getPublicKey().equals(publicKey);
    }
}   
