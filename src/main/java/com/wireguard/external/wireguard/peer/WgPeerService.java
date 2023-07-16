package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.ISubnet;
import com.wireguard.external.network.ISubnetSolver;
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

import java.util.*;

@Component
@Scope("singleton")
public class WgPeerService {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    WgPeerGenerator peerGenerator;
    RepositoryPageable<WgPeer> wgPeerRepository;
    ISubnetSolver wgSubnetSolver;
    PeerCreationRules peerCreationRules;


    @Autowired
    public WgPeerService(WgPeerGenerator peerGenerator, RepositoryPageable<WgPeer> wgPeerRepository,
                         ISubnetSolver wgSubnetSolver, PeerCreationRules peerCreationRules) {
        this.peerGenerator = peerGenerator;
        this.wgPeerRepository = wgPeerRepository;
        this.wgSubnetSolver = wgSubnetSolver;
        this.peerCreationRules = peerCreationRules;
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
        HashSet<ISubnet> allowedIps = new HashSet<>(peerCreationRequest.getAllowedIps());
        obtainSubnets(IpUtils.filterV4Ips(allowedIps));
        allowedIps.addAll(generateSubnets(peerCreationRequest.getCountOfIpsToGenerate(), peerCreationRules.getDefaultMask()));
        peerCreationRequest.setAllowedIps(allowedIps);
        CreatedPeer createdPeer = peerGenerator.createPeerGenerateNulls(peerCreationRequest);
        wgPeerRepository.add(WgPeer.publicKey(createdPeer.getPublicKey())
                .presharedKey(createdPeer.getPresharedKey())
                .allowedIps(createdPeer.getAllowedSubnets())
                .persistentKeepalive(createdPeer.getPersistentKeepalive())
                .build());
        return createdPeer;
    }


    private Set<Subnet> generateSubnets(int countOfIpsToGenerate, int mask) {
        Set<Subnet> subnets = new HashSet<>();
        for (int i = 0; i < countOfIpsToGenerate; i++) {
            subnets.add(wgSubnetSolver.obtainFree(mask));
        }
        return subnets;
    }

    private void obtainSubnets(Set<Subnet> subnets) {
        Set<Subnet> obtainedSubnets = new HashSet<>();
        try{
            subnets.forEach((subnet) -> {
                wgSubnetSolver.obtain(subnet);
                obtainedSubnets.add(subnet);
            });
        } catch (Exception e){
            obtainedSubnets.forEach(wgSubnetSolver::release);
            throw e;
        }
    }


    public WgPeer updatePeer(PeerUpdateRequest updateRequest) {
        WgPeer oldPeer = wgPeerRepository.getBySpecification(new FindByPublicKey(updateRequest.getCurrentPublicKey()))
                .stream()
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException("Peer with public key %s not found".formatted(updateRequest.getCurrentPublicKey()))
                );
        throwIfPeerExists(updateRequest.getNewPublicKey());
        WgPeer.Builder newPeerBuilder = WgPeer.from(oldPeer);
        newPeerBuilder.publicKey(
                updateRequest.getNewPublicKey() != null ? updateRequest.getNewPublicKey() : oldPeer.getPublicKey());
        newPeerBuilder.presharedKey(defaultIfNull(updateRequest.getPresharedKey(), oldPeer.getPresharedKey()));
        newPeerBuilder.allowedIps(defaultIfNull(updateRequest.getAllowedIps(), oldPeer.getAllowedSubnets().getAll()));
        newPeerBuilder.endpoint(defaultIfNull(updateRequest.getEndpoint(), oldPeer.getEndpoint()));
        newPeerBuilder.persistentKeepalive(defaultIfNull(updateRequest.getPersistentKeepalive(), oldPeer.getPersistentKeepalive()));
        WgPeer newPeer = newPeerBuilder.build();
        obtainNewSubnets(oldPeer, newPeer);
        wgPeerRepository.update(oldPeer, newPeer);
        return newPeer;
    }

    private void obtainNewSubnets(WgPeer oldPeer, WgPeer newPeer) {
      /*  Set<Subnet> oldSubnets = oldPeer.getAllowedSubnets().getSubnets();
        Set<Subnet> newSubnets = newPeer.getAllowedSubnets().getSubnets();
        Set<Subnet> subnetsToObtain = new HashSet<>(newSubnets);
        subnetsToObtain.removeAll(oldSubnets);
        obtainSubnets(subnetsToObtain);*/
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
        wgPeerRepository.remove(
                WgPeer.publicKey(publicKey).build()
        );
    }


}
