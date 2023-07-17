package com.wireguard.external.wireguard.peer.spec;

import com.wireguard.external.wireguard.Specification;
import com.wireguard.external.wireguard.peer.WgPeer;
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
