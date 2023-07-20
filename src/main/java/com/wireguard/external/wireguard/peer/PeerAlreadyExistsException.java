package com.wireguard.external.wireguard.peer;

public class PeerAlreadyExistsException extends RuntimeException {
    public PeerAlreadyExistsException(String publicKey) {
        super("Peer with public key %s already exists".formatted(publicKey));
    }
}
