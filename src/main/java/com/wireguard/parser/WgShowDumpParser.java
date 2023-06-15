package com.wireguard.parser;


import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.WgShowDump;
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

    public static WgShowDump fromDump(String dump) {
        logger.trace("Parsing dump %s".formatted(dump));
        Scanner scanner = new Scanner(dump);
        if (!scanner.hasNextLine()) {
            throw new IllegalArgumentException("Dump is empty");
        }
        String dumpHeader = scanner.nextLine();
        try {
            WgInterfaceDTO wgInterfaceDTO = parseInterface(dumpHeader);
            List<WgPeer> peers = parsePeers(scanner);
            return new WgShowDump(wgInterfaceDTO, peers);
        } catch (Exception e) {
            logger.error("Error while parsing dump \n%s".formatted(dump));
            throw e;
        }
    }


    private static List<WgPeer> parsePeers(Scanner peersDump) {
        List<WgPeer> peers = new ArrayList<WgPeer>();
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
