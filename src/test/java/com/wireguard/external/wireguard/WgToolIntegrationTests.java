package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@EnabledOnOs(OS.LINUX)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WgToolIntegrationTests {
    private final static ShellRunner shellRunner = new ShellRunner();
    private final static String interfaceName = "wg_cont_test";
    private final static File wgConfigFileSource = new File("src/test/resources/%s.conf".formatted(interfaceName));
    private static File wgConfigFile;
    private final static String interfacePublicKey = "sBdtuH6Q84CmecM+A832NOyAb9Oz0W7rJdPCR/JS63I=";
    private final static WgTool wgTool = new WgTool(0);

    @BeforeAll
    static void setUpEnvironment() throws IOException {
        wgConfigFile = new File("/etc/wireguard/%s.conf".formatted(interfaceName));
        FileInputStream wgConfigFileSourceStream = new FileInputStream(wgConfigFileSource);
        FileOutputStream wgConfigFileOutputStream = new FileOutputStream(wgConfigFile);
        wgConfigFileOutputStream.write(wgConfigFileSourceStream.readAllBytes());
        wgConfigFileOutputStream.close();
        wgConfigFileSourceStream.close();


        String resultOfCommand = shellRunner.execute(new String[]{"sudo","wg-quick", "up", wgConfigFile.getAbsolutePath()}, List.of(0,1));
        Assertions.assertFalse(resultOfCommand.contains("wg-quick:"));
    }

    @AfterAll
    static void tearDownEnvironment() {
        String resultOfCommand = shellRunner.execute(new String[]{"sudo","wg-quick", "down", wgConfigFile.getAbsolutePath()}, List.of(0,1));
        boolean isDeleted = wgConfigFile.delete();
        Assertions.assertFalse(resultOfCommand.contains("wg-quick:"));
        Assertions.assertTrue(isDeleted);
    }
    @Test
    @Order(5)
    void showDump() throws IOException {
        WgShowDump dump = wgTool.showDump(interfaceName);
        Assertions.assertNotNull(dump);
        Assertions.assertEquals(interfacePublicKey, dump.wgInterfaceDTO().getPrivateKey());
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
    @Order(10)
    void addPeer() throws IOException {
        String privateKey = wgTool.generatePrivateKey();
        String publicKey = wgTool.generatePublicKey(privateKey);
        String presharedKey = wgTool.generatePresharedKey();
        String allowedIps = "10.112.112.10/32";
        CreatedPeer createdPeer = new CreatedPeer(publicKey, presharedKey, privateKey,
                Set.of(allowedIps), 0);
        wgTool.addPeer(interfaceName, createdPeer);
        WgShowDump dump = wgTool.showDump(interfaceName);
        Optional<WgPeer> addedPeer = dump.peers().stream().filter(peer -> peer.getPublicKey().equals(publicKey)).findFirst();
        Assertions.assertTrue(addedPeer.isPresent());
        Assertions.assertEquals(publicKey, addedPeer.get().getPublicKey());
        Assertions.assertEquals(presharedKey, addedPeer.get().getPresharedKey());
        Assertions.assertEquals(allowedIps, addedPeer.get().getAllowedIps().toString());
    }

    @Test
    @Order(15)
    void saveConfig()  {
        String config = wgTool.showConf(interfaceName);
        Assertions.assertNotNull(config);
        Assertions.assertTrue(config.contains("10.112.112.10/32"));
    }

}