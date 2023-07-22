package com.wireguard.external.wireguard.peer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wireguard.external.network.IV4SubnetSolver;
import com.wireguard.external.network.NetworkInterfaceData;
import com.wireguard.external.wireguard.RepositoryPageable;
import com.wireguard.external.wireguard.Specification;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import lombok.SneakyThrows;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class CachedWgPeerRepository extends WgPeerRepository implements RepositoryPageable<WgPeer> {

    private final Cache<String, WgPeer> wgPeerCache;
    private final ScheduledExecutorService cacheUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ReentrantReadWriteLock lock =
            new ReentrantReadWriteLock(true);

    @Autowired
    public CachedWgPeerRepository(WgTool wgTool, NetworkInterfaceData wgInterface, IV4SubnetSolver subnetSolver,
                                  @Value("${wg.cache.update-interval}") int cacheUpdateIntervalSeconds) {
        super(wgTool, wgInterface);
        wgPeerCache = Caffeine.newBuilder().build();
        cacheUpdateScheduler.scheduleAtFixedRate(() -> updateCache(subnetSolver),
                0, cacheUpdateIntervalSeconds, TimeUnit.SECONDS);


    }

    @Override
    public void add(WgPeer wgPeer) {
        wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
        super.add(wgPeer);
    }

    
    @Override
    public void remove(WgPeer wgPeer) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            wgPeerCache.invalidate(wgPeer.getPublicKey());
            super.remove(wgPeer);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void update(WgPeer oldT, WgPeer newT) {
        if (!oldT.getPublicKey().equals(newT.getPublicKey()))
            wgPeerCache.invalidate(oldT.getPublicKey());
        wgPeerCache.put(newT.getPublicKey(), newT);
        super.update(oldT, newT);
    }

    @Override
    public List<WgPeer> getByAllSpecifications(List<Specification<WgPeer>> specifications) {
        Optional<FindByPublicKey> findByPublicKeySpec = specifications.stream()
                .filter(spec -> spec instanceof FindByPublicKey)
                .map(spec -> (FindByPublicKey) spec)
                .findFirst();
        List<WgPeer> peers = new ArrayList<>();
        if (findByPublicKeySpec.isPresent()){
            Optional<WgPeer> peer = getFromCacheByPublicKey(findByPublicKeySpec.get().getPublicKey());
            specifications = new ArrayList<>(specifications);
            specifications.remove(findByPublicKeySpec.get());
            if (peer.isPresent()) peers.add(peer.get());
        } else {
            peers = getAll();
        }
        return super.getByAllSpecifications(specifications, peers);
    }

    private Optional<WgPeer> getFromCacheByPublicKey(String publicKey){
        Optional<WgPeer> peer = Optional.ofNullable(wgPeerCache.getIfPresent(publicKey));
        return peer;
    }

    
    synchronized private void updateCache(IV4SubnetSolver subnetSolver) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            List<WgPeer> newPeers = super.getAll();
            newPeers.forEach(wgPeer -> {
                wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
                wgPeer.getAllowedSubnets().getIPv4Subnets().stream()
                        .filter(subnet -> !subnetSolver.isUsed(subnet))
                        .forEach(subnetSolver::obtain);
            });
        } finally {
            writeLock.unlock();
        }
    }

    
    @Override
    public List<WgPeer> getAll() {
        return wgPeerCache.asMap().values().stream().toList();

    }

}
