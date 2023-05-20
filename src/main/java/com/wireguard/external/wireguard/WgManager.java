package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.WgInterface;
import com.wireguard.external.wireguard.WgShowDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class WgManager {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private String interfaceName = "wg0";
    private static WgTool wgTool;

    @Autowired
    public WgManager(WgTool wgTool) {
        WgManager.wgTool = wgTool;
    }




    public WgInterface getInterface() throws ParsingException {
        return getDump().getWgInterface();
    }

    public List<WgPeer> getPeers() throws ParsingException {
        return getDump().getPeers();
    }

    private WgShowDump getDump() throws ParsingException {
        try{
            return wgTool.showDump(interfaceName);
        } catch (IOException e) {
            logger.error("Error getting dump", e);
            throw new ParsingException("Error while getting dump", e);
        }
    }

    public Optional<WgPeer> getPeerByPublicKey(String publicKey) throws ParsingException {
        List<WgPeer> peers = getPeers();
        WgPeerContainer wgPeerContainer = new WgPeerContainer(peers);
        WgPeer wgPeer = wgPeerContainer.getByPublicKey(publicKey);
        return Optional.ofNullable(wgPeer);
    }


}
