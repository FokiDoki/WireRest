package com.wirerest.wireguard.events;

import com.wirerest.wireguard.peer.WgPeer;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PeerUpdatedEvent extends ApplicationEvent {

    private final WgPeer oldPeer;
    private final WgPeer newPeer;

    public PeerUpdatedEvent(Object source, WgPeer oldPeer, WgPeer newPeer) {
        super(source);
        this.oldPeer = oldPeer;
        this.newPeer = newPeer;
    }


}
