package com.wireguard.external.wireguard;

import com.wireguard.external.network.ISubnet;
import com.wireguard.external.network.IV4SubnetSolver;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.network.SubnetV6;
import com.wireguard.external.shell.StreamToStringConverter;
import com.wireguard.external.wireguard.peer.PeerCreationRules;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.peer.WgPeerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Component
public class SubnetService {

    private static final Logger logger = LoggerFactory.getLogger(SubnetService.class);
    PeerCreationRules peerCreationRules;
    IV4SubnetSolver v4SubnetSolver;

    @Autowired
    public SubnetService(PeerCreationRules peerCreationRules, IV4SubnetSolver v4SubnetSolver) {
        this.peerCreationRules = peerCreationRules;
        this.v4SubnetSolver = v4SubnetSolver;
    }

    public Set<Subnet> generateV4(int countOfIpsToGenerate, int mask) {
        Set<Subnet> subnets = new HashSet<>();
        releaseIfException(subnets, (s) -> {
            for (int i = 0; i < countOfIpsToGenerate; i++) {
                subnets.add(v4SubnetSolver.obtainFree(mask));
            }
        });
        logger.debug("Generated subnets: {}", subnets);
        return Collections.unmodifiableSet(subnets);
    }

    public Set<Subnet> generateV4(int countOfIpsToGenerate) {
        return generateV4(countOfIpsToGenerate, peerCreationRules.getDefaultMask());
    }



    public Set<SubnetV6> generateV6(int countOfIpsToGenerate, int mask) {
        throw new UnsupportedOperationException("It doesn't support yet");
    }

    private void releaseIfException(Set<? extends ISubnet> subnets, Consumer<Set<? extends ISubnet>> consumer) {
        try{
            consumer.accept(subnets);
        } catch (Exception e){
            logger.debug("Exception occurred during obtaining subnets, releasing subnets: {}", subnets);
            release(subnets);
            throw e;
        }
    }

    public void release(Set<? extends ISubnet> subnets) {
        subnets.forEach((subnet) -> {
            logger.debug("Releasing subnet: {}", subnet);
            if (subnet instanceof Subnet) {
                v4SubnetSolver.release((Subnet) subnet);
            } else if (subnet instanceof SubnetV6) {

            }
        });
    }

    public void obtain(Set<? extends ISubnet> subnets) {
        Set<ISubnet> obtainedSubnets = new HashSet<>();
        releaseIfException(obtainedSubnets, (s) -> {
            logger.debug("Obtaining subnets: {}", subnets);
            subnets.forEach((subnet) -> {
                if (subnet instanceof Subnet){
                    v4SubnetSolver.obtain((Subnet) subnet);
                } else if (subnet instanceof SubnetV6){

                }
                obtainedSubnets.add(subnet);
            });
        });
    }

    public void applyState(Set<? extends ISubnet> oldState, Set<? extends ISubnet> newState) {
        Set<ISubnet> subnetsToRelease = new HashSet<>(oldState);
        subnetsToRelease.removeAll(newState);
        release(subnetsToRelease);
        Set<ISubnet> subnetsToObtain = new HashSet<>(newState);
        subnetsToObtain.removeAll(oldState);
        obtain(subnetsToObtain);
        logger.debug("Applied subnet state, old state: {}, new state: {}", oldState, newState);
    }


}
