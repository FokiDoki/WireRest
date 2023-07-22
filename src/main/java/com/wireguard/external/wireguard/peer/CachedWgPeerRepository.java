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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "true")
public class CachedWgPeerRepository extends WgPeerRepository implements RepositoryPageable<WgPeer> {

    private final Cache<String, WgPeer> wgPeerCache;
    private final ScheduledExecutorService cacheUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean cacheUpdateInProgress = new AtomicBoolean(false);
    private final ReentrantLock cacheReplacing = new ReentrantLock();
    private ReentrantLock mutex = new ReentrantLock();
    private final List<String> peersToDeleteAfterCacheUpdate = new LinkedList<>();

    @Autowired
    public CachedWgPeerRepository(WgTool wgTool, NetworkInterfaceData wgInterface, IV4SubnetSolver subnetSolver,
                                  @Value("${wg.cache.update-interval}") int cacheUpdateIntervalSeconds) {
        super(wgTool, wgInterface);
        wgPeerCache = Caffeine.newBuilder()
                .build();
        cacheUpdateScheduler.scheduleAtFixedRate(() -> updateCache(subnetSolver),
                0, cacheUpdateIntervalSeconds, TimeUnit.SECONDS);


    }

    @Override
    public void add(WgPeer wgPeer) {
        wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
        super.add(wgPeer);
    }

    @SneakyThrows
    @Override
    public void remove(WgPeer wgPeer) {
        cacheReplacing.lock();
        mutex.lock();
        wgPeerCache.invalidate(wgPeer.getPublicKey());
        mutex.unlock();
        System.out.println("Removing peer from cache "+wgPeer.getPublicKey());
        super.remove(wgPeer);
        if (cacheUpdateInProgress.get()){
            peersToDeleteAfterCacheUpdate.add(wgPeer.getPublicKey());
            System.out.println("added to delete list "+wgPeer.getPublicKey());
        }
        cacheReplacing.unlock();
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
        mutex.lock();
        System.out.println("Getting peer from cache "+publicKey);
        System.out.println("Cache size "+wgPeerCache.estimatedSize());
        System.out.println("Cache state "+wgPeerCache.asMap().keySet().size());
       // System.out.println("cache state "+wgPeerCache.asMap().keySet().stream().reduce("", (s, s2) -> s+" "+s2));
        Optional<WgPeer> peer = Optional.ofNullable(wgPeerCache.getIfPresent(publicKey));
        mutex.unlock();
        return peer;
    }

    @SneakyThrows
    synchronized private void updateCache(IV4SubnetSolver subnetSolver) {
        System.out.println("Getting all peers");
        cacheUpdateInProgress.set(true);
        List<WgPeer> newPeers = super.getAll();
        System.out.println("Got all peers "+newPeers.stream().map(WgPeer::getPublicKey).reduce("", (s, s2) -> s+" "+s2));

        Thread.sleep(1000);
        System.out.println("Updating cache");
        System.out.println(peersToDeleteAfterCacheUpdate.size() + " peers to no add");
        cacheReplacing.lock();
        newPeers.forEach(wgPeer -> {
            //System.out.println(peersToDeleteAfterCacheUpdate);
            if (peersToDeleteAfterCacheUpdate.contains(wgPeer.getPublicKey())){
                peersToDeleteAfterCacheUpdate.remove(wgPeer.getPublicKey());
                System.out.println("Peer "+wgPeer.getPublicKey()+" not added");
                return;
            }

            wgPeerCache.put(wgPeer.getPublicKey(), wgPeer);
            //System.out.println("a "+wgPeer.getPublicKey());
            wgPeer.getAllowedSubnets().getIPv4Subnets().stream()
                    .filter(subnet -> !subnetSolver.isUsed(subnet))
                    .forEach(subnetSolver::obtain);
        });
        cacheReplacing.unlock();
        System.out.println(peersToDeleteAfterCacheUpdate.size()+ " peers to no add");
        cacheUpdateInProgress.set(false);
        System.out.println("Cache updated");
        peersToDeleteAfterCacheUpdate.clear();
    }

    @SneakyThrows
    @Override
    public List<WgPeer> getAll() {
        cacheReplacing.lock();
        mutex.lock();
        List<WgPeer> peers = wgPeerCache.asMap().values().stream().toList();
       // System.out.println("getall: "+peers.stream().map(WgPeer::getPublicKey).collect(Collectors.toList()));
        mutex.unlock();
        cacheReplacing.unlock();
        return peers;

    }

}
