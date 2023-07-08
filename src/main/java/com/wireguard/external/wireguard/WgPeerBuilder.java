package com.wireguard.external.wireguard;

import com.wireguard.external.network.IpResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WgPeerBuilder {

    @Value("${wg.interface.new_client_subnet_mask}")
    private static int DEFAULT_MASK;
    private IpResolver wgIpResolver;

    public WgPeerBuilder(IpResolver wgIpResolver) {
        this.wgIpResolver = wgIpResolver;
    }
}
