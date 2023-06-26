package com.wireguard.parser;


import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.WgShowDump;
import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


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
            WgInterfaceDTO wgInterfaceDTO = parseInterface(dumpHeader);
            Set<WgPeer> peers = parsePeers(dump);
            return new WgShowDump(wgInterfaceDTO, peers);
        } catch (Exception e) {
            logger.error("Error while parsing dump \n%s".formatted(dump.toString()));
            throw e;
        }
    }


    private static Set<WgPeer> parsePeers(Scanner peersDump) {
        Set<WgPeer> peers = new HashSet<>();
        while (peersDump.hasNextLine()) {
            String line = peersDump.nextLine();
            logger.trace("Parsing peer "+line);
            WgPeer peer = WgPeerParser.parse(line, SPLITTER);
            peers.add(peer);
        }
        return peers;
    }

    private static WgInterfaceDTO parseInterface(String wgShowInterfaceLine) {
        logger.trace("Parsing interface "+wgShowInterfaceLine);
        Assert.notNull(wgShowInterfaceLine, "Wg interface dump is null");
        return WgInterfaceParser.parse(wgShowInterfaceLine, SPLITTER);
    }



}
