package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.ISubnet;
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
    private AllowedSubnets allowedSubnets;
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
                ", allowedSubnets=" + allowedSubnets.toString() +
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
    public static class AllowedSubnets implements Comparable<AllowedSubnets>{
        private Set<Subnet> IPv4Subnets = new HashSet<>();
        private Set<String> IPv6Subnets = new HashSet<>();

        public void addIpv4(String allowedIPv4Ip){
            IPv4Subnets.add(Subnet.valueOf(allowedIPv4Ip));
        }

        public void addIpv6(String allowedIPv6Ip){
            IPv6Subnets.add(allowedIPv6Ip);
        }

        public Set<String> getAll(){
            Set<String> allowedIps = new HashSet<>();
            IPv4Subnets.forEach(subnet -> allowedIps.add(subnet.toString()));
            allowedIps.addAll(IPv6Subnets);
            return Collections.unmodifiableSet(allowedIps);
        }
        @Override
        public String toString(){
            return String.join(",", getAll());
        }
        public boolean isEmpty(){
            return IPv4Subnets.isEmpty() && IPv6Subnets.isEmpty();
        }

        @Override
        public int compareTo(AllowedSubnets o) {
            return IPv4Subnets.stream().findFirst().orElse(Subnet.valueOf("0.0.0.0/32"))
                    .compareTo(o.IPv4Subnets.stream().findFirst().orElse(Subnet.valueOf("0.0.0.0/32")));
        }
    }


    public static class Builder{
        private String publicKey;
        private String presharedKey;
        private String endpoint;
        private AllowedSubnets allowedSubnets = new AllowedSubnets();
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

        public Builder allowedIPv4Subnets(Set<Subnet> allowedIPv4Ips) {
            this.allowedSubnets.setIPv4Subnets(allowedIPv4Ips);
            return this;
        }

        public Builder allowedIPv6Subnets(Set<String> allowedIPv6Ips) {
            this.allowedSubnets.setIPv6Subnets(allowedIPv6Ips);
            return this;
        }

        public Builder allowedIps(Set<ISubnet> allowedSubnets) {
            allowedSubnets.stream()
                    .filter(subnet -> subnet instanceof Subnet)
                    .map(subnet -> (Subnet) subnet)
                    .forEach(this.allowedSubnets.IPv4Subnets::add);
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
                    allowedSubnets,
                    latestHandshake,
                    transferRx,
                    transferTx,
                    persistentKeepalive);
        }

    }

}