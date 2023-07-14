package com.wireguard.external.wireguard.iface;

import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.wireguard.WgTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "false")
public class WgInterfaceService {

    private final NetworkInterfaceDTO wgInterface;
    private final WgTool wgTool;

    public WgInterfaceService(NetworkInterfaceDTO wgInterface, WgTool wgTool) {
        this.wgInterface = wgInterface;
        this.wgTool = wgTool;
    }

    public WgInterface getInterface() {
        return wgTool.showDump(wgInterface.getName()).wgInterface();
    }
}
