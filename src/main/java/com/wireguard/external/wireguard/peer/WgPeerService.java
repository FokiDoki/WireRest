package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.ISubnet;
import com.wireguard.external.network.IV4SubnetSolver;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.*;
import com.wireguard.external.wireguard.peer.spec.FindByPublicKey;
import com.wireguard.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@Scope("singleton")
@Service
public class WgPeerService {

    private static final Logger logger = LoggerFactory.getLogger(WgPeerService.class);
    WgPeerGenerator peerGenerator;
    RepositoryPageable<WgPeer> wgPeerRepository;
    SubnetService subnetService;


    @Autowired
    public WgPeerService(WgPeerGenerator peerGenerator, RepositoryPageable<WgPeer> wgPeerRepository,
                         SubnetService subnetService) {
        this.peerGenerator = peerGenerator;
        this.wgPeerRepository = wgPeerRepository;
        this.subnetService = subnetService;
    }


    public Optional<WgPeer> getPeerByPublicKey(String publicKey) throws ParsingException {
        return wgPeerRepository
                .getBySpecification(new FindByPublicKey(publicKey)).stream()
                .findFirst();
    }

    public WgPeer getPeerByPublicKeyOrThrow(String publicKey) throws ParsingException {
        return getPeerByPublicKey(publicKey).orElseThrow(
                () -> new NoSuchElementException("Peer with public key %s not found".formatted(publicKey))
        );
    }

    public List<WgPeer> getPeers() {
        return wgPeerRepository.getAll();
    }

    public Page<WgPeer> getPeers(Pageable pageable) {
        return wgPeerRepository.getAll(pageable);
    }

    public CreatedPeer createPeerGenerateNulls(PeerCreationRequest peerCreationRequest) {
        HashSet<ISubnet> allowedIps = new HashSet<>(peerCreationRequest.getAllowedIps());
        subnetService.obtain(allowedIps);
        allowedIps.addAll(subnetService.generateV4(peerCreationRequest.getCountOfIpsToGenerate()));
        peerCreationRequest.setAllowedIps(allowedIps);

        CreatedPeer createdPeer = peerGenerator.createPeerGenerateNulls(peerCreationRequest);
        wgPeerRepository.add(createdPeerToWgPeer(createdPeer));
        return createdPeer;
    }

    private WgPeer createdPeerToWgPeer(CreatedPeer createdPeer) {
        return WgPeer.publicKey(createdPeer.getPublicKey())
                .presharedKey(createdPeer.getPresharedKey())
                .allowedIps(createdPeer.getAllowedSubnets())
                .persistentKeepalive(createdPeer.getPersistentKeepalive())
                .build();
    }


    private boolean isUpdateRequestHasNewPublicKey(PeerUpdateRequest updateRequest) {
        return updateRequest.getNewPublicKey() != null &&
                updateRequest.getNewPublicKey().equals(updateRequest.getCurrentPublicKey());
    }


    public WgPeer updatePeer(PeerUpdateRequest updateRequest) {
        WgPeer oldPeer = getPeerByPublicKeyOrThrow(updateRequest.getCurrentPublicKey());
        if (isUpdateRequestHasNewPublicKey(updateRequest)) throwIfPeerExists(updateRequest.getNewPublicKey());
        WgPeer.Builder newPeerBuilder = WgPeer.from(oldPeer);
        newPeerBuilder.publicKey(
                updateRequest.getNewPublicKey() != null ? updateRequest.getNewPublicKey() : oldPeer.getPublicKey());
        newPeerBuilder.presharedKey(defaultIfNull(updateRequest.getPresharedKey(), oldPeer.getPresharedKey()));
        newPeerBuilder.allowedIps(defaultIfNull(updateRequest.getAllowedIps(), oldPeer.getAllowedSubnets().getAll()));
        newPeerBuilder.endpoint(defaultIfNull(updateRequest.getEndpoint(), oldPeer.getEndpoint()));
        newPeerBuilder.persistentKeepalive(defaultIfNull(updateRequest.getPersistentKeepalive(), oldPeer.getPersistentKeepalive()));
        WgPeer newPeer = newPeerBuilder.build();
        subnetService.applyState(oldPeer.getAllowedSubnets().getAll(), newPeer.getAllowedSubnets().getAll());
        wgPeerRepository.update(oldPeer, newPeer);
        return newPeer;
    }


    private void throwIfPeerExists(String publicKey) {
        wgPeerRepository.getBySpecification(new FindByPublicKey(publicKey))
                .stream()
                .findFirst().ifPresent(
                        peer -> {
                            throw new IllegalArgumentException("Peer with public key %s already exists".formatted(publicKey));
                        }
                );
    }

    public <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }


    public CreatedPeer createPeer() {
        return createPeerGenerateNulls(new EmptyPeerCreationRequest());
    }

    public void deletePeer(String publicKey) {
        WgPeer peer = getPeerByPublicKeyOrThrow(publicKey);
        wgPeerRepository.remove(
                WgPeer.publicKey(publicKey).build()
        );
        subnetService.release(peer.getAllowedSubnets().getAll());
    }


}
