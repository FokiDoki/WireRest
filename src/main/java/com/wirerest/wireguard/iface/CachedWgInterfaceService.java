package com.wirerest.wireguard.iface;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wirerest.network.NetworkInterfaceData;
import com.wirerest.wireguard.WgTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
@Component
public class CachedWgInterfaceService extends WgInterfaceService {

    private final int REFRESH_INTERVAL_SECONDS = 300;

    LoadingCache<String, WgInterface> wgInterfaceCache;

    public CachedWgInterfaceService(NetworkInterfaceData wgInterface, WgTool wgTool) {
        super(wgInterface, wgTool);
        wgInterfaceCache = Caffeine.newBuilder()
                .refreshAfterWrite(REFRESH_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .build(key -> super.getInterface());
    }

    @Override
    public WgInterface getInterface() {
        return wgInterfaceCache.get("");
    }
}
