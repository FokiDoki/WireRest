package com.wireguard.external.wireguard;

import com.wireguard.external.network.ISubnet;
import com.wireguard.external.network.Subnet;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.util.Set;

@Data
@AllArgsConstructor
public class IpAllocationRequest {
    private Set<? extends ISubnet> subnets;
    private final Integer countOfIpsToGenerate;

}
