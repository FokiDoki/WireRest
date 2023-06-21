package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Scope("singleton")
public class WgManager {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private final WgInterface wgInterface;
    @Value("${wg.interface.new_client_subnet_mask}")
    private int defaultMaskForNewClients = 32;
    private final IpResolver wgIpResolver;
    private static WgTool wgTool;

    @Autowired
    public WgManager(WgTool wgTool, IpResolver wgIpResolver, WgInterface wgInterface) {
        WgManager.wgTool = wgTool;
        this.wgIpResolver = wgIpResolver;
        this.wgInterface = wgInterface;
    }


    public WgInterfaceDTO getInterface()  {
        return getDump().wgInterfaceDTO();
    }



    private WgShowDump getDump() {
        try{
            return wgTool.showDump(wgInterface.name());
        } catch (IOException e) {
            logger.error("Error getting dump", e);
            throw new ParsingException("Error while getting dump", e);
        }
    }

    public Optional<WgPeerDTO> getPeerDTOByPublicKey(String publicKey) throws ParsingException {
        WgPeerContainer peerContainer = getWgPeerContainer();
        return peerContainer.getDTOByPublicKey(publicKey);
    }

    private WgPeerContainer getWgPeerContainer()  {
        List<WgPeer> peers = getDump().peers();
        return new WgPeerContainer(peers);
    }

    public Set<WgPeerDTO> getPeers(){
        WgPeerContainer peerContainer = getWgPeerContainer();
        return peerContainer.toDTOSet();
    }

    public CreatedPeer createPeer(){
        String privateKey = wgTool.generatePrivateKey().strip();
        String publicKey = wgTool.generatePublicKey(privateKey.strip()).strip();
        String presharedKey = wgTool.generatePresharedKey().strip();
        Subnet address = wgIpResolver.takeFreeSubnet(defaultMaskForNewClients);
        wgTool.addPeer(wgInterface.name(), publicKey, presharedKey, address.toString(), 0);
        return new CreatedPeer(
                publicKey,
                presharedKey,
                privateKey,
                Set.of(address.toString()),
                0
        );
    }

    public void deletePeer(String publicKey) {
        wgTool.deletePeer(wgInterface.name(), publicKey);
    }
}
