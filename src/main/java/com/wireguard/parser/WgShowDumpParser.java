package com.wireguard.parser;


import com.wireguard.external.wireguard.iface.WgInterface;
import com.wireguard.external.wireguard.peer.WgPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class WgShowDumpParser {

    private static final Logger logger = LoggerFactory.getLogger(WgShowDumpParser.class);
    private static final String SPLITTER = "\t";


    private WgShowDumpParser() {
    }

    public static WgShowDump fromDump(Scanner dump) {
        logger.trace("Parsing dump.. ");
        if (!dump.hasNextLine()) {
            throw new IllegalArgumentException("Dump is empty");
        }
        String dumpHeader = dump.nextLine();
        try {
            WgInterface wgInterface = parseInterface(dumpHeader);
            List<WgPeer> peers = parsePeers(dump);
            return new WgShowDump(wgInterface, peers);
        } catch (Exception e) {
            logger.error("Error while parsing dump \n%s".formatted(dump.toString()));
            throw e;
        }
    }


    private static List<WgPeer> parsePeers(Scanner peersDump) {
        List<WgPeer> peers = new ArrayList<>();
        while (peersDump.hasNextLine()) {
            String line = peersDump.nextLine();
            logger.trace("Parsing peer "+line);
            WgPeer peer = WgPeerParser.parse(line, SPLITTER);
            peers.add(peer);
        }
        return peers;
    }

    private static WgInterface parseInterface(String wgShowInterfaceLine) {
        logger.trace("Parsing interface "+wgShowInterfaceLine);
        Assert.notNull(wgShowInterfaceLine, "Wg interface dump is null");
        return WgInterfaceParser.parse(wgShowInterfaceLine, SPLITTER);
    }



}
