package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.dto.WgPeer;
import com.wireguard.external.wireguard.dto.WgShowDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class WgInterfaceController {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private String interfaceName = "wg0";
    private static WgTool wgTool;

    @Autowired
    public WgInterfaceController(WgTool wgTool) {
        WgInterfaceController.wgTool = wgTool;
    }




    public List<WgPeer> getAllPeers() throws IOException {
        WgShowDump wgShowDump = wgTool.showDump(interfaceName);
        return wgShowDump.getPeers();
    }


}
