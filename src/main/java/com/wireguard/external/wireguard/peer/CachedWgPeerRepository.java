package com.wireguard.external.wireguard.peer;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wireguard.external.network.ISubnetSolver;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.wireguard.RepositoryPageable;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class CachedWgPeerRepository extends WgPeerRepository implements RepositoryPageable<WgPeer> {

    private final LoadingCache<String, WgPeer> wgPeerCache;
    private final ScheduledExecutorService cacheUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
    private final int UPDATE_INTERVAL_SECONDS;

    @Autowired
    public CachedWgPeerRepository(WgTool wgTool, NetworkInterfaceDTO wgInterface, ISubnetSolver subnetSolver,
                                  @Value("${wg.cache.update-interval}") int cacheUpdateIntervalSeconds) {
        super(wgTool, wgInterface);
        UPDATE_INTERVAL_SECONDS = cacheUpdateIntervalSeconds;
        wgPeerCache = Caffeine.newBuilder()
                .refreshAfterWrite(UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .build(key -> super.getBySpecification(new FindByPublicKey(key)).stream().findFirst().orElse(null));
        cacheUpdateScheduler.scheduleAtFixedRate(() -> updateCache(subnetSolver), 0, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);

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
        if (!oldT.getPublicKey().equals(newT.getPublicKey()))
            wgPeerCache.invalidate(oldT.getPublicKey());
        wgPeerCache.put(newT.getPublicKey(), newT);
        super.update(oldT, newT);
    }

    private void updateCache(ISubnetSolver subnetSolver) {
        wgPeerCache.invalidateAll();
        for (WgPeer wgPeer : super.getAll()) {
            wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
            wgPeer.getAllowedSubnets().getIPv4Subnets().stream()
                    .filter(subnet -> !subnetSolver.isUsed(subnet))
                    .forEach(subnetSolver::obtain);
        }
    }

    @Override
    public List<WgPeer> getAll() {
        return wgPeerCache.asMap().values().stream().toList();
    }

}
