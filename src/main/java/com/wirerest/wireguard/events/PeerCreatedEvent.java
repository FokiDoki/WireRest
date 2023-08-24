package com.wirerest.wireguard.events;

import com.wirerest.wireguard.peer.CreatedPeer;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PeerCreatedEvent extends ApplicationEvent {

    private final CreatedPeer peer;
    public PeerCreatedEvent(Object source, CreatedPeer peer) {
        super(source);
        this.peer = peer;
    }


}
