package com.wirerest.metrics;

import com.wirerest.wireguard.peer.WgPeer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class Utils {
    public Transfer calculateTransfer(List<WgPeer> peers) {
        Transfer.Builder transfer = new Transfer.Builder();
        for (WgPeer peer : peers) {
            transfer.addTx(peer.getTransferTx());
            transfer.addRx(peer.getTransferRx());
        }
        return transfer.build();
    }

    @AllArgsConstructor
    @Getter
    public static class Transfer {
        private final long tx;
        private final long rx;
        public static class Builder{
            private long tx = 0;
            private long rx = 0;

            public void addTx(long tx){
                this.tx += tx;
            }

            public void addRx(long rx){
                this.rx += rx;
            }
            public Transfer build(){
                return new Transfer(tx, rx);
            }
        }

    }
}
