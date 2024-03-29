package com.wirerest.parser;


import com.wirerest.shell.StreamToStringConverter;
import com.wirerest.wireguard.parser.WgShowDump;
import com.wirerest.wireguard.parser.WgShowDumpParser;
import com.wirerest.wireguard.peer.WgPeer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

class WgShowDumpParserTest {
    private Scanner wgShowDump;
    //public-key, preshared-key, endpoint, allowed-ips, latest-handshake, transfer-rx, transfer-tx, persistent-keepalive.
    private static final String FIRST_PEER_ALLOWED_IPS = "10.66.66.2/32,fd42:42:42::2/128";
    private static final String FIRST_PEER_ENDPOINT = "90.255.84.234:62517";
    private static final int FIRST_PEER_PERSISTENT_KEEPALIVE = 0;
    private static final Long FIRST_PEER_LAST_HANDSHAKE_TIME = 1683030306L;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        File wgshowdumpFile = new File("src/test/resources/wg_show_dump.txt");
        StreamToStringConverter streamToStringConverter = new StreamToStringConverter();
        wgShowDump = new Scanner(new FileInputStream(wgshowdumpFile));
    }

    @Test
    void fromDumpTest() throws IOException {
        WgShowDump dump = WgShowDumpParser.fromDump(wgShowDump);

        Assertions.assertEquals(16666, dump.wgInterface().getListenPort());
        Assertions.assertEquals("Ds123123312G859AO3s1I8vhTgrgrgrgrgrgt9LKF8B=", dump.wgInterface().getPrivateKey());
        Assertions.assertEquals("Z1xHdYc+enfengren+nvrenbvnbmegw3gjrejgvfnvn=", dump.wgInterface().getPublicKey());
    }

    @Test
    void fromDumpTestPeerParsing() throws IOException {
        WgShowDump dump = WgShowDumpParser.fromDump(wgShowDump);
        Assertions.assertEquals(11, dump.peers().size());
        WgPeer peer = dump.peers().stream().filter(p -> p.getPublicKey().equals("8avysqyA1N+IIX8d1gergergergergbHf2TfuEfw4ff=")).findFirst().get();
        Assertions.assertEquals(FIRST_PEER_ENDPOINT, peer.getEndpoint());
        Assertions.assertEquals(Set.of("10.66.66.2/32", "fd42:42:42::2/128"), peer.getAllowedSubnets().getAllStrings());
        Assertions.assertEquals(FIRST_PEER_PERSISTENT_KEEPALIVE, peer.getPersistentKeepalive());
        Assertions.assertEquals(FIRST_PEER_LAST_HANDSHAKE_TIME, peer.getLatestHandshake());

    }

    @Test
    void isConstructorPrivate() {
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            WgShowDumpParser.class.getDeclaredConstructor().newInstance();
        });
    }

    @Test
    void emptyDumpTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            WgShowDumpParser.fromDump(new Scanner(""));
        });
    }

    @Test
    void invalidDumpTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            WgShowDumpParser.fromDump(new Scanner("invalid dump"));
        });
    }

}