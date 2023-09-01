package com.wirerest.wireguard.peer;


import com.wirerest.api.ResourceNotFoundException;

public class PeerNotFoundException extends ResourceNotFoundException {
    public PeerNotFoundException(String publicKey) {
        super("Peer with public key %s not found".formatted(publicKey));
    }
}
