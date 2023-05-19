package com.wireguard.parser;


import com.wireguard.external.wireguard.dto.WgInterface;
import com.wireguard.external.wireguard.dto.WgPeer;
import com.wireguard.external.wireguard.dto.WgShowDump;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

@Data
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
            WgInterface wgInterface = parseInterface(dumpHeader);
            List<WgPeer> peers = parsePeers(scanner);
            return new WgShowDump(wgInterface, peers);
        } catch (Exception e) {
            logger.error("Error while parsing dump %s".formatted(dump));
            throw e;
        }
    }

    private static String tryReadLine(Scanner scanner) {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        } else {
            throw new NoSuchElementException("Scanner has no more lines");
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

    private static WgInterface parseInterface(String wgShowInterfaceLine) {
        logger.trace("Parsing interface "+wgShowInterfaceLine);
        Assert.notNull(wgShowInterfaceLine, "Wg interface dump is null");
        return WgInterfaceParser.parse(wgShowInterfaceLine, SPLITTER);
    }

}
