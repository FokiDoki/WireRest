package com.wirerest.wireguard.peer;


import com.wirerest.network.NetworkInterfaceData;
import com.wirerest.wireguard.Paging;
import com.wirerest.wireguard.RepositoryPageable;
import com.wirerest.wireguard.Specification;
import com.wirerest.wireguard.WgTool;
import com.wirerest.wireguard.events.PeerCreatedEvent;
import com.wirerest.wireguard.events.PeerDeletedEvent;
import com.wirerest.wireguard.events.PeerUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "wg.cache.enabled", havingValue = "false")
public class WgPeerRepository implements RepositoryPageable<WgPeer> {

    private final WgTool wgTool;
    private final NetworkInterfaceData wgInterface;
    protected final ApplicationEventPublisher applicationEventPublisher;
    private final Paging<WgPeer> paging = new Paging<>(WgPeer.class);

    @Autowired
    public WgPeerRepository(WgTool wgTool, NetworkInterfaceData wgInterface, ApplicationEventPublisher applicationEventPublisher) {
        this.wgTool = wgTool;
        this.wgInterface = wgInterface;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void add(WgPeer wgPeer) {
        wgTool.addPeer(wgInterface.getName(), wgPeer);
        applicationEventPublisher.publishEvent(new PeerCreatedEvent(this, wgPeer));
    }

    @Override
    public void remove(WgPeer wgPeer) {
        wgTool.deletePeer(wgInterface.getName(), wgPeer.getPublicKey());
        applicationEventPublisher.publishEvent(new PeerDeletedEvent(this, wgPeer));
    }

    @Override
    public void update(WgPeer oldT, WgPeer newT) {
        if (!oldT.getPublicKey().equals(newT.getPublicKey())) {
            wgTool.deletePeer(wgInterface.getName(), oldT.getPublicKey());
        }
        wgTool.addPeer(wgInterface.getName(), newT);
        applicationEventPublisher.publishEvent(new PeerUpdatedEvent(this, oldT, newT));
    }

    @Override
    public List<WgPeer> getBySpecification(Specification<WgPeer> specification) {
        return getByAllSpecifications(List.of(specification));
    }

    @Override
    public List<WgPeer> getByAllSpecifications(List<Specification<WgPeer>> specifications) {
        return getByAllSpecifications(specifications, getAll());
    }

    protected List<WgPeer> getByAllSpecifications(List<Specification<WgPeer>> specifications, List<WgPeer> peers) {
        return peers.stream()
                .filter(wgPeer ->
                        specifications.stream().allMatch(
                                specification -> specification.isExist(wgPeer)
                        )
                ).collect(Collectors.toList());
    }


    @Override
    public List<WgPeer> getAll() {
        return wgTool.showDump(wgInterface.getName()).peers();
    }


    @Override
    public Page<WgPeer> getAll(Pageable pageable) {
        List<WgPeer> peers = getAll();
        return paging.apply(pageable, peers);
    }

}
