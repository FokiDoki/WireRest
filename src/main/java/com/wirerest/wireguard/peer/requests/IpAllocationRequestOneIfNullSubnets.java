package com.wirerest.wireguard.peer.requests;

import com.wirerest.network.ISubnet;

import java.util.Objects;
import java.util.Set;

public class IpAllocationRequestOneIfNullSubnets extends IpAllocationRequest {

    public IpAllocationRequestOneIfNullSubnets(Set<? extends ISubnet> subnets) {
        super(
                Objects.requireNonNullElse(subnets, Set.of()),
                subnets == null ? 1 : 0
        );
    }
}
