package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.Subnet;
import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.*;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Component
@Scope("singleton")
public class WgPeerService {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    WgPeerCreator wgPeerCreator;
    RepositoryPageable<WgPeer> wgPeerRepository;

    @Autowired
    public WgPeerService(WgPeerCreator wgPeerCreator, RepositoryPageable<WgPeer> wgPeerRepository) {
        this.wgPeerCreator = wgPeerCreator;
        this.wgPeerRepository = wgPeerRepository;
    }


    public Optional<WgPeer> getPeerByPublicKey(String publicKey) throws ParsingException {
        return wgPeerRepository
                .getBySpecification(new FindByPublicKey(publicKey)).stream()
                .findFirst();
    }

    public List<WgPeer> getPeers() {
        return wgPeerRepository.getAll();
    }

    public Page<WgPeer> getPeers(Pageable pageable) {
        return wgPeerRepository.getAll(pageable);
    }

    public CreatedPeer createPeerGenerateNulls(PeerCreationRequest peerCreationRequest) {
        CreatedPeer createdPeer = wgPeerCreator.createPeerGenerateNulls(peerCreationRequest);
        wgPeerRepository.add(WgPeer.publicKey(createdPeer.getPublicKey())
                .presharedKey(createdPeer.getPresharedKey())
                .allowedIPv4Subnets(createdPeer.getAllowedSubnets())
                .persistentKeepalive(createdPeer.getPersistentKeepalive())
                .build());
        return createdPeer;
    }



    public WgPeer updatePeer(PeerUpdateRequest updateRequest) {
        WgPeer oldPeer = wgPeerRepository.getBySpecification(new FindByPublicKey(updateRequest.getCurrentPublicKey()))
                .stream()
                .findFirst().orElseThrow();
        WgPeer.Builder newPeerBuilder = WgPeer.from(oldPeer);
        newPeerBuilder.publicKey(
                updateRequest.getNewPublicKey() != null ? updateRequest.getNewPublicKey() : oldPeer.getPublicKey());
        consumeIfNotNullElseDefault(updateRequest.getPresharedKey(), oldPeer.getPresharedKey(), psk -> newPeerBuilder.presharedKey((String) psk));
        consumeIfNotNullElseDefault(updateRequest.getAllowedV4Ips(), oldPeer.getAllowedSubnets().getIPv4Subnets(),
                subnets -> newPeerBuilder.allowedIPv4Subnets((Set<Subnet>) subnets));
        consumeIfNotNullElseDefault(updateRequest.getAllowedV6Ips(), oldPeer.getAllowedSubnets().getIPv6Subnets(),
                subnets -> newPeerBuilder.allowedIPv6Subnets((Set<String>) subnets));
        consumeIfNotNullElseDefault(updateRequest.getEndpoint(), oldPeer.getEndpoint(),
                endpoint -> newPeerBuilder.endpoint((String) endpoint));
        consumeIfNotNullElseDefault(updateRequest.getPersistentKeepalive(), oldPeer.getPersistentKeepalive(),
                pk -> newPeerBuilder.persistentKeepalive((Integer) pk));
        WgPeer newPeer = newPeerBuilder.build();
        wgPeerRepository.update(oldPeer, newPeer);
        return newPeer;
    }

    private void consumeIfNotNullElseDefault(@Nullable Object object, Object defaultVal, Consumer<Object> consumer) {
        if (object != null) {
            consumer.accept(object);
        } else {
            consumer.accept(defaultVal);
        }
    }


    public CreatedPeer createPeer() {
        return createPeerGenerateNulls(new EmptyPeerCreationRequest());
    }

    public void deletePeer(String publicKey) {
        wgPeerRepository.remove(
                WgPeer.publicKey(publicKey).build()
        );
    }


}
