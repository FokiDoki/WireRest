package com.wireguard.external.wireguard.peer;

import com.wireguard.api.ResourceNotFoundException;

public class PeerNotFoundException extends ResourceNotFoundException {
    public PeerNotFoundException(String publicKey) {
        super("Peer with public key %s not found".formatted(publicKey));
    }
}
