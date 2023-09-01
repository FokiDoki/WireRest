package com.wirerest.wireguard.events;

import com.wirerest.wireguard.peer.Transfer;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SyncTransferUpdatedEvent extends ApplicationEvent {

    private final Transfer transfer;

    public SyncTransferUpdatedEvent(Object source, Transfer transfer) {
        super(source);
        this.transfer = transfer;
    }


}
