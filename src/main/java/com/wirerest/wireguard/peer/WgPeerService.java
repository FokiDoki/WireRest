package com.wirerest.wireguard.peer;

import com.wirerest.network.ISubnet;
import com.wirerest.wireguard.ParsingException;
import com.wirerest.wireguard.RepositoryPageable;
import com.wirerest.wireguard.SubnetService;
import com.wirerest.wireguard.peer.requests.EmptyPeerCreationRequest;
import com.wirerest.wireguard.peer.requests.IpAllocationRequest;
import com.wirerest.wireguard.peer.requests.PeerCreationRequest;
import com.wirerest.wireguard.peer.requests.PeerUpdateRequest;
import com.wirerest.wireguard.peer.spec.FindByPublicKey;
import com.wirerest.wireguard.tools.BlockingByHashAsyncExecutor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.wirerest.utils.AsyncUtils.await;

@Component
@Scope("singleton")
@Service
public class WgPeerService {

    private static final Logger logger = LoggerFactory.getLogger(WgPeerService.class);
    private final WgPeerGenerator peerGenerator;
    private final RepositoryPageable<WgPeer> wgPeerRepository;
    private final SubnetService subnetService;
    private final BlockingByHashAsyncExecutor<WgPeer> blockingByHashAsyncExecutor = new BlockingByHashAsyncExecutor<>();



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
                () -> new PeerNotFoundException(publicKey)
        );
    }

    public List<WgPeer> getPeers() {
        return wgPeerRepository.getAll();
    }

    public Page<WgPeer> getPeers(Pageable pageable) {
        return wgPeerRepository.getAll(pageable);
    }

    public CreatedPeer createPeerGenerateNulls(PeerCreationRequest peerCreationRequest) {
        if (peerCreationRequest.getPublicKey() != null) throwIfPeerExists(peerCreationRequest.getPublicKey());
        IpAllocationRequest ipAllocationRequest = peerCreationRequest.getIpAllocationRequest();
        HashSet<ISubnet> allowedIps = new HashSet<>(ipAllocationRequest.getSubnets());
        subnetService.obtain(allowedIps);
        allowedIps.addAll(subnetService.generateV4(ipAllocationRequest.getCountOfIpsToGenerate()));
        ipAllocationRequest.setSubnets(allowedIps);

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
                !updateRequest.getNewPublicKey().equals(updateRequest.getCurrentPublicKey());
    }


    @SneakyThrows
    public WgPeer updatePeer(PeerUpdateRequest updateRequest) {
        Future<WgPeer> peer = blockingByHashAsyncExecutor.addTask(updateRequest.getCurrentPublicKey(),
                () -> updatePeerTask(updateRequest));
        return await(peer);
    }

    private WgPeer updatePeerTask(PeerUpdateRequest updateRequest) {
        WgPeer oldPeer = getPeerByPublicKeyOrThrow(updateRequest.getCurrentPublicKey()).clone();
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
                            throw new PeerAlreadyExistsException(peer.getPublicKey());
                        }
                );
    }

    private <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }


    public CreatedPeer createPeer() {
        return createPeerGenerateNulls(new EmptyPeerCreationRequest());
    }

    public WgPeer deletePeer(String publicKey) {
        WgPeer peer = getPeerByPublicKeyOrThrow(publicKey);
        wgPeerRepository.remove(peer);
        subnetService.release(peer.getAllowedSubnets().getAll());
        return peer;
    }


}
