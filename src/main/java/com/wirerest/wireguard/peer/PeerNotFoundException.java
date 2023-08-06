package com.wirerest.wireguard.peer;


import org.webjars.NotFoundException;

public class PeerNotFoundException extends NotFoundException {
    public PeerNotFoundException(String publicKey) {
        super("Peer with public key %s not found".formatted(publicKey));
    }
}
