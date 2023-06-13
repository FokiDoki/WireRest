package com.wireguard.external.wireguard;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;



@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class WgPeer {
    private String publicKey;
    private String presharedKey;
    private String endpoint;
    private AllowedIps allowedIps;
    private long latestHandshake;
    private long transferRx;
    private long transferTx;
    private int persistentKeepalive;

    public static Builder withPublicKey(String publicKey) {
        return new Builder().publicKey(publicKey);
    }

    @Override
    public String toString() {
        return "WgPeer{" +
                "publicKey='" + publicKey + '\'' +
                ", presharedKey='" + presharedKey + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", allowedIps=" + allowedIps.toString() +
                ", latestHandshake=" + latestHandshake +
                ", transferRx=" + transferRx +
                ", transferTx=" + transferTx +
                ", persistentKeepalive=" + persistentKeepalive +
                '}';
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AllowedIps{
        private Set<String> IPv4IPs = new HashSet<>();
        private Set<String> IPv6IPs = new HashSet<>();

        public void addIpv4(String allowedIPv4Ip){
            IPv4IPs.add(allowedIPv4Ip);
        }

        public void addIpv6(String allowedIPv6Ip){
            IPv6IPs.add(allowedIPv6Ip);
        }

        public Set<String> getAll(){
            Set<String> allowedIps = new HashSet<>();
            allowedIps.addAll(IPv4IPs);
            allowedIps.addAll(IPv6IPs);
            return allowedIps;
        }
        @Override
        public String toString(){
            return String.join(",", getAll());
        }
        public boolean isEmpty(){
            return IPv4IPs.isEmpty() && IPv6IPs.isEmpty();
        }
    }


    public static class Builder{
        private String publicKey;
        private String presharedKey;
        private String endpoint;
        private AllowedIps allowedIps = new AllowedIps();
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

        public Builder allowedIPv4Ips(Set<String> allowedIPv4Ips) {
            this.allowedIps.setIPv4IPs(allowedIPv4Ips);
            return this;
        }

        public Builder allowedIPv6Ips(Set<String> allowedIPv6Ips) {
            this.allowedIps.setIPv6IPs(allowedIPv6Ips);
            return this;
        }

        public Builder allowedIps(AllowedIps allowedIps) {
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