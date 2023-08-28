package com.wirerest.wireguard.events;

import com.wirerest.wireguard.peer.WgPeer;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PeerCreatedEvent extends ApplicationEvent {

    private final WgPeer peer;
    public PeerCreatedEvent(Object source, WgPeer peer) {
        super(source);
        this.peer = peer;
    }


}
