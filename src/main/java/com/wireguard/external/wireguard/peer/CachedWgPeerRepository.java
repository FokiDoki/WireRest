package com.wireguard.external.wireguard.peer;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.wireguard.RepositoryPageable;
import com.wireguard.external.wireguard.Specification;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(value = "api.cache.enabled", havingValue = "true")
public class CachedWgPeerRepository extends WgPeerRepository implements RepositoryPageable<WgPeer> {

    private final LoadingCache<String, WgPeer> wgPeerCache;
    private final ScheduledExecutorService cacheUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
    private final static int UPDATE_INTERVAL_SECONDS = 10;


    @Autowired
    public CachedWgPeerRepository(WgTool wgTool, NetworkInterfaceDTO wgInterface) {
        super(wgTool, wgInterface);
        wgPeerCache = Caffeine.newBuilder()
                .refreshAfterWrite(UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .build(key -> super.getBySpecification(new FindByPublicKey(key)).stream().findFirst().orElse(null));
        cacheUpdateScheduler.scheduleAtFixedRate(this::updateCache, 0, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);

    }

    @Override
    public void add(WgPeer wgPeer) {
        wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
        super.add(wgPeer);
    }

    @Override
    public void remove(WgPeer wgPeer) {
        wgPeerCache.invalidate(wgPeer.getPublicKey());
        super.remove(wgPeer);
    }

    @Override
    public void update(WgPeer oldT, WgPeer newT) {
        wgPeerCache.put(newT.getPublicKey(), newT);
        super.update(oldT, newT);
    }

    private void updateCache() {
        wgPeerCache.invalidateAll();
        for (WgPeer wgPeer : super.getAll()) {
            wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
        }
    }

    @Override
    public List<WgPeer> getAll() {
        return wgPeerCache.asMap().values().stream().toList();
    }

}
