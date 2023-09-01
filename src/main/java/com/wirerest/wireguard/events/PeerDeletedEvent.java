package com.wirerest.wireguard.events;

import com.wirerest.wireguard.peer.WgPeer;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PeerDeletedEvent extends ApplicationEvent {

    private final WgPeer peer;
    public PeerDeletedEvent(Object source, WgPeer peer) {
        super(source);
        this.peer = peer;
    }


}
