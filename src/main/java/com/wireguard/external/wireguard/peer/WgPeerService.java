package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.NetworkInterfaceDTO;
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

    public List<WgPeer> getPeers(){
        return wgPeerRepository.getAll();
    }

    public Page<WgPeer> getPeers(Pageable pageable) {
        return wgPeerRepository.getAll(pageable);
    }

    public CreatedPeer createPeerGenerateNulls(PeerCreationRequest peerCreationRequest){
        CreatedPeer createdPeer = wgPeerCreator.createPeerGenerateNulls(peerCreationRequest);
        wgPeerRepository.add(WgPeer.publicKey(createdPeer.getPublicKey())
                .presharedKey(createdPeer.getPresharedKey())
                .allowedIPv4Subnets(createdPeer.getAllowedSubnets())
                .persistentKeepalive(createdPeer.getPersistentKeepalive())
                .build());
        return createdPeer;
    }

    public CreatedPeer createPeer(){
        return createPeerGenerateNulls(new EmptyPeerCreationRequest());
    }

    public void deletePeer(String publicKey)  {
        wgPeerRepository.remove(
                WgPeer.publicKey(publicKey).build()
        );
    }


}
