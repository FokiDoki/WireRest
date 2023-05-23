package com.wireguard.external.wireguard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WgPeer {
    private String publicKey;
    public String presharedKey;
    public String endpoint;
    public String allowedIps;
    public long latestHandshake;
    public long transferRx;
    public long transferTx;
    public int persistentKeepalive;

    public static Builder withPublicKey(String publicKey){
        return new Builder().publicKey(publicKey);
    }


    public static class Builder{
        private String publicKey;
        private String presharedKey;
        private String endpoint;
        private String allowedIps;
        private long latestHandshake;
        private long transferRx;
        private long transferTx;
        private int persistentKeepalive;

        protected Builder(){}


        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder presharedKey(String presharedKey) {
            this.presharedKey = presharedKey;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder allowedIps(String allowedIps) {
            this.allowedIps = allowedIps;
            return this;
        }

        public Builder latestHandshake(long latestHandshake) {
            this.latestHandshake = latestHandshake;
            return this;
        }

        public Builder transferRx(long transferRx) {
            this.transferRx = transferRx;
            return this;
        }

        public Builder transferTx(long transferTx) {
            this.transferTx = transferTx;
            return this;
        }

        public Builder persistentKeepalive(int persistentKeepalive) {
            this.persistentKeepalive = persistentKeepalive;
            return this;
        }

        public WgPeer build(){
            Assert.notNull(publicKey, "Public key must not be null");
            Assert.notNull(presharedKey, "Preshared key must not be null");
            Assert.notNull(allowedIps, "allowed Ips must not be null");
            return new WgPeer(publicKey,
                    presharedKey,
                    endpoint,
                    allowedIps,
                    latestHandshake,
                    transferRx,
                    transferTx,
                    persistentKeepalive);
        }

    }

}