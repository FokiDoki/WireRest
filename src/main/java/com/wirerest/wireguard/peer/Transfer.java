package com.wirerest.wireguard.peer;

import lombok.Getter;

@Getter
public class Transfer {
    private final long tx;
    private final long rx;

    public Transfer(long tx, long rx) {
        this.tx = tx;
        this.rx = rx;
    }

    public static class Builder {
        private long tx = 0;
        private long rx = 0;

        public void addTx(long tx) {
            this.tx += tx;
        }

        public void addRx(long rx) {
            this.rx += rx;
        }

        public Transfer build() {
            return new Transfer(tx, rx);
        }
    }


}
