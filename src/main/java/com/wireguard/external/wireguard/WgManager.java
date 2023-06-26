package com.wireguard.external.wireguard;

import com.wireguard.external.network.IpResolver;
import com.wireguard.external.network.NetworkInterfaceDTO;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.converters.WgPeerContainerToWgPeerDTOSet;
import com.wireguard.external.wireguard.converters.WgPeerIterableToWgPeerDTOList;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Scope("singleton")
public class WgManager {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private final NetworkInterfaceDTO wgInterface;
    @Value("${wg.interface.new_client_subnet_mask}")
    private int defaultMaskForNewClients = 32;
    private final IpResolver wgIpResolver;
    private static WgTool wgTool;
    WgPeerContainerToWgPeerDTOSet containerToPeer = new WgPeerContainerToWgPeerDTOSet();
    WgPeerIterableToWgPeerDTOList iterableToPeer = new WgPeerIterableToWgPeerDTOList();

    @Autowired
    public WgManager(WgTool wgTool, IpResolver wgIpResolver, NetworkInterfaceDTO wgInterface) {
        WgManager.wgTool = wgTool;
        this.wgIpResolver = wgIpResolver;
        this.wgInterface = wgInterface;
    }


    public WgInterfaceDTO getInterface()  {
        return getDump().wgInterfaceDTO();
    }



    private WgShowDump getDump() {
        logger.debug("Dump requested for interface %s".formatted(wgInterface.getName()));
        return wgTool.showDump(wgInterface.getName());
    }

    private Optional<WgPeerDTO> getPeerDTOOptional(Optional<WgPeer> wgPeer){
        if (wgPeer.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(WgPeerDTO.from(wgPeer.get()));
        }
    }

    public Optional<WgPeerDTO> getPeerDTOByPublicKey(String publicKey) throws ParsingException {
        WgPeerContainer peerContainer = getWgPeerContainer();
        return getPeerDTOOptional(peerContainer.getByPublicKey(publicKey));
    }

    public Optional<WgPeer> getPeerByPublicKey(String publicKey) throws ParsingException {
        WgPeerContainer peerContainer = getWgPeerContainer();
        return peerContainer.getByPublicKey(publicKey);
    }

    private WgPeerContainer getWgPeerContainer()  {
        Set<WgPeer> peers = getDump().peers();
        return new WgPeerContainer(peers);
    }

    public Set<WgPeerDTO> getPeers(){
        WgPeerContainer peerContainer = getWgPeerContainer();
        return containerToPeer.convert(peerContainer);
    }

    public List<WgPeerDTO> getPeers(Sort sort){
        WgPeerContainer peerContainer = getWgPeerContainer();
        return iterableToPeer.convert(peerContainer.findAll(sort));
    }

    public List<WgPeerDTO> getPeers(Pageable pageable){
        WgPeerContainer peerContainer = getWgPeerContainer();
        Page<WgPeer> page = peerContainer.findAll(pageable);
        return iterableToPeer.convert(page);
    }


    public CreatedPeer createPeer(){
        String privateKey = wgTool.generatePrivateKey();
        String publicKey = wgTool.generatePublicKey(privateKey);
        String presharedKey = wgTool.generatePresharedKey();
        Subnet address = wgIpResolver.takeFreeSubnet(defaultMaskForNewClients);
        CreatedPeer createdPeer = new CreatedPeer(
                publicKey,
                presharedKey,
                privateKey,
                Set.of(address.toString()),
                0);
        try {
            wgTool.addPeer(wgInterface.getName(), createdPeer);
            logger.info("Created peer, public key: %s".formatted(publicKey.substring(0, 6)));
        } catch (Exception e){
            wgIpResolver.freeSubnet(address);
            throw e;
        }
        return createdPeer;
    }

    public void deletePeer(String publicKey)  {

        wgTool.deletePeer(wgInterface.getName(), publicKey);
        logger.info("Deleted peer, public key: %s".formatted(publicKey));
    }
}
