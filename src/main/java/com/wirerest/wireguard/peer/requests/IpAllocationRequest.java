package com.wirerest.wireguard.peer.requests;

import com.wirerest.network.ISubnet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class IpAllocationRequest {
    private Set<? extends ISubnet> subnets;
    private final Integer countOfIpsToGenerate;

}
