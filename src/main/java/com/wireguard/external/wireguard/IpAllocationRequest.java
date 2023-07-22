package com.wireguard.external.wireguard;

import com.wireguard.external.network.ISubnet;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class IpAllocationRequest {
    private final Integer countOfIpsToGenerate;
    private Set<? extends ISubnet> subnets;

}
