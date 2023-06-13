package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@EnabledOnOs(OS.LINUX)
class WgToolTests {
    private final static ShellRunner shellRunner = new ShellRunner();
    private final static String interfaceName = "wg-cont-test";
    private final static String interfacePublicKey = "sBdtuH6Q84CmecM+A832NOyAb9Oz0W7rJdPCR/JS63I=";
    private final static WgTool wgTool = new WgTool();
    @BeforeEach
    void setUpEnvironment() {
        File wgConfigFile = new File("src/test/resources/%s.conf".formatted(interfaceName));
        String pathToWgConfig = wgConfigFile.getAbsolutePath();
        String resultOfCommand = shellRunner.execute(new String[]{"sudo","wg-quick", "up", pathToWgConfig});
        Assertions.assertFalse(resultOfCommand.contains("wg-quick:"));
    }

    @AfterAll
    static void tearDownEnvironment() {
        String resultOfCommand = shellRunner.execute(new String[]{"sudo","wg-quick", "down", interfaceName});
        Assertions.assertFalse(resultOfCommand.contains("wg-quick:"));
    }
    @Test
    void showDump() throws IOException {
        WgShowDump dump = wgTool.showDump(interfaceName);
        Assertions.assertNotNull(dump);
        Assertions.assertEquals(interfacePublicKey, dump.wgInterface().getPublicKey());
        Assertions.assertEquals(3, dump.peers().size());
    }

    @Test
    void generatePrivateKey() {
        String privateKey = wgTool.generatePrivateKey();
        Assertions.assertNotNull(privateKey);
        Assertions.assertEquals(44, privateKey.length());
    }

    @Test
    void generatePublicKey() {
        String privateKey = wgTool.generatePrivateKey();
        String publicKey = wgTool.generatePublicKey(privateKey);
        Assertions.assertNotNull(publicKey);
        Assertions.assertEquals(44, publicKey.length());
    }

    @Test
    void generatePresharedKey() {
        String presharedKey = wgTool.generatePresharedKey();
        Assertions.assertNotNull(presharedKey);
        Assertions.assertEquals(44, presharedKey.length());
    }

    @Test
    void addPeer() throws IOException {
        String privateKey = wgTool.generatePrivateKey();
        String publicKey = wgTool.generatePublicKey(privateKey);
        String presharedKey = wgTool.generatePresharedKey();
        String allowedIps = "10.112.112.10/32";
        wgTool.addPeer(interfaceName, publicKey, presharedKey, allowedIps, 0);
        WgShowDump dump = wgTool.showDump(interfaceName);
        Optional<WgPeer> addedPeer = dump.peers().stream().filter(peer -> peer.getPublicKey().equals(publicKey)).findFirst();
        Assertions.assertTrue(addedPeer.isPresent());
        Assertions.assertEquals(publicKey, addedPeer.get().getPublicKey());
        Assertions.assertEquals(presharedKey, addedPeer.get().getPresharedKey());
        Assertions.assertEquals(allowedIps, addedPeer.get().getAllowedIps().toString());
    }
}