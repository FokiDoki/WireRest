package com.wireguard.external.wireguard;

import com.wireguard.external.network.Subnet;
import lombok.*;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WgPeer implements Comparable<WgPeer> {
    private String publicKey;
    private String presharedKey;
    private String endpoint;
    private AllowedIps allowedIps;
    private long latestHandshake;
    private long transferRx;
    private long transferTx;
    private int persistentKeepalive;
    public static Builder publicKey(String publicKey) {
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

    @Override
    public int compareTo(WgPeer o) {
        return this.publicKey.compareTo(o.publicKey);
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AllowedIps implements Comparable<AllowedIps>{
        private Set<Subnet> IPv4IPs = new HashSet<>();
        private Set<String> IPv6IPs = new HashSet<>();

        public void addIpv4(String allowedIPv4Ip){
            IPv4IPs.add(Subnet.valueOf(allowedIPv4Ip));
        }

        public void addIpv6(String allowedIPv6Ip){
            IPv6IPs.add(allowedIPv6Ip);
        }

        public Set<String> getAll(){
            Set<String> allowedIps = new HashSet<>();
            IPv4IPs.forEach(subnet -> allowedIps.add(subnet.toString()));
            allowedIps.addAll(IPv6IPs);
            return Collections.unmodifiableSet(allowedIps);
        }
        @Override
        public String toString(){
            return String.join(",", getAll());
        }
        public boolean isEmpty(){
            return IPv4IPs.isEmpty() && IPv6IPs.isEmpty();
        }

        @Override
        public int compareTo(AllowedIps o) {
            return IPv4IPs.stream().findFirst().orElse(Subnet.valueOf("0.0.0.0/32"))
                    .compareTo(o.IPv4IPs.stream().findFirst().orElse(Subnet.valueOf("0.0.0.0/32")));
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
            this.allowedIps.setIPv4IPs(allowedIPv4Ips.stream()
                    .map(Subnet::valueOf)
                    .collect(Collectors.toSet()
                    ));
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