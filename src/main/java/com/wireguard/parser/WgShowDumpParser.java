package com.wireguard.parser;

import com.wireguard.DTO.WgInterface;
import com.wireguard.DTO.WgPeer;
import com.wireguard.DTO.WgShowDump;
import com.wireguard.external.UbuntuCommandExecutor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class WgShowDumpParser {

    private static final Logger logger = LoggerFactory.getLogger(WgShowDumpParser.class);

    private static final String SPLITTER = "\t";
    private static final Function<String, List<String>> splitAndReplaceOffs =
            (String s) -> List.of(s.replaceAll("off", "0").split(SPLITTER));
    private WgShowDumpParser() {
    }

    public static WgShowDump fromDump(BufferedReader dump) throws IOException {
        logger.trace("Parsing wg show dump");
        WgInterface wgInterface = parseInterface(dump);
        List<WgPeer> peers = parsePeers(dump);
        return new WgShowDump(wgInterface, peers);
    }

    private static List<WgPeer> parsePeers(BufferedReader peersDump) throws IOException {
        List<WgPeer> peers = new ArrayList<WgPeer>();
        String line;
        while ((line = peersDump.readLine()) != null && !line.isEmpty()) {
            logger.trace("Parsing peer "+line);
            peers.add(WgPeerParser.parse(splitAndReplaceOffs.apply(line)));
        }
        return peers;
    }

    private static WgInterface parseInterface(BufferedReader interfaceDump) throws IOException {
        String line = interfaceDump.readLine();
        logger.trace("Parsing interface "+line);
        Assert.notNull(line, "Wg conf header is empty, invalid config");
        return WgInterfaceParser.parse(splitAndReplaceOffs.apply(line));
    }



}
