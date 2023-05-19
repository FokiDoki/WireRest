package com.wireguard.parser;


import com.wireguard.converters.StreamToStringConverter;
import com.wireguard.external.wireguard.WgPeerContainer;
import com.wireguard.external.wireguard.dto.WgPeer;
import com.wireguard.external.wireguard.dto.WgShowDump;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;

class WgShowDumpParserTest {
    private String wgShowDump;
//public-key, preshared-key, endpoint, allowed-ips, latest-handshake, transfer-rx, transfer-tx, persistent-keepalive.
    private static final String FIRST_PEER_ALLOWED_IPS = "10.66.66.2/32,fd42:42:42::2/128";
    private static final String FIRST_PEER_ENDPOINT = "90.255.84.234:62517";
    private static final int FIRST_PEER_PERSISTENT_KEEPALIVE = 0;
    private static final Instant FIRST_PEER_LAST_HANDSHAKE_TIME = Instant.ofEpochSecond(1683030306);
    @BeforeEach
    void setUp() throws FileNotFoundException {
        File wgshowdumpFile = new File("src/test/resources/wg_show_dump.txt");
        StreamToStringConverter streamToStringConverter = new StreamToStringConverter();
        wgShowDump = streamToStringConverter.convert(new FileInputStream(wgshowdumpFile));
    }

    @Test
    void fromDumpTest() throws IOException {
        WgShowDump dump = WgShowDumpParser.fromDump(wgShowDump);

        Assertions.assertEquals(16666, dump.getWgInterface().getListenPort());
        Assertions.assertEquals("Ds123123312G859AO3s1I8vhTgrgrgrgrgrgt9LKF8B=", dump.getWgInterface().getPrivateKey());
        Assertions.assertEquals("Z1xHdYc+enfengren+nvrenbvnbmegw3gjrejgvfnvn=", dump.getWgInterface().getPublicKey());
    }
    @Test
    void fromDumpTestPeerParsing() throws IOException {
        WgShowDump dump = WgShowDumpParser.fromDump(wgShowDump);
        WgPeerContainer wgPeers = new WgPeerContainer(dump.getPeers());
        Assertions.assertEquals(11, wgPeers.size());
        WgPeer peer = dump.getPeers().get(0);
        Assertions.assertEquals(FIRST_PEER_ENDPOINT, peer.getEndpoint().getHostName()+":"+peer.getEndpoint().getPort());
        Assertions.assertEquals(FIRST_PEER_ALLOWED_IPS, peer.getAllowedIps());
        Assertions.assertEquals(FIRST_PEER_PERSISTENT_KEEPALIVE, peer.getPersistentKeepalive());
        Assertions.assertEquals(FIRST_PEER_LAST_HANDSHAKE_TIME, peer.getLatestHandshake());

    }

}