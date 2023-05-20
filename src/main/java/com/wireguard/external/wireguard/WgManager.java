package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.dto.WgInterface;
import com.wireguard.external.wireguard.dto.WgShowDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WgManager {

    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private String interfaceName = "wg0";
    private static WgTool wgTool;

    @Autowired
    public WgManager(WgTool wgTool) {
        WgManager.wgTool = wgTool;
    }




    public WgInterface getInterface() throws BadInterfaceException {
        try{
            WgShowDump wgShowDump = wgTool.showDump(interfaceName);
            return wgShowDump.getWgInterface();
        } catch (IOException e) {
            logger.error("Error getting dump", e);
            throw new BadInterfaceException("Error while getting interface", e);
        }
    }


}
