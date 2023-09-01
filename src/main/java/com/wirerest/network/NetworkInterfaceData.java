package com.wirerest.network;

import lombok.Getter;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class NetworkInterfaceData {
    private final String name;


    private final Set<Subnet> ipv4Subnets = new HashSet<>();
    private final Set<Inet4Address> ipv4Addresses = new HashSet<>();

    public NetworkInterfaceData(String name) {
        this.name = name;
    }

    public Set<Subnet> getCidrV4Address() {
        return Collections.unmodifiableSet(ipv4Subnets);
    }

    public Set<Inet4Address> getIpv4Addresses() {
        return Collections.unmodifiableSet(ipv4Addresses);
    }

    public void addInterfaceAddress(InterfaceAddress interfaceAddress) {
        if (interfaceAddress.getAddress() instanceof Inet4Address)
            ipv4Subnets.add(Subnet.valueOf(interfaceAddress.getAddress(),
                    interfaceAddress.getNetworkPrefixLength()));
        else throw new IllegalArgumentException("Only IPv4 addresses are supported");
    }

    public void addInterfaceAddress(Subnet subnet) {
        ipv4Subnets.add(subnet);
    }

    public void addAddress(Inet4Address address) {
        ipv4Addresses.add(address);
    }
}
