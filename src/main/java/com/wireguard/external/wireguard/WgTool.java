package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.WgShowDump;
import com.wireguard.parser.WgShowDumpParser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

@Profile("prod")
@Component
public class WgTool {

    private static final String WG_SHOW_DUMP_COMMAND = "wg show %s dump";
    private static final String WG_GENKEY_COMMAND = "wg genkey";
    private static final String WG_PUBKEY_COMMAND = "wg pubkey | echo %s";
    private final ShellRunner shell = new ShellRunner();


    public WgShowDump showDump(String interfaceName) throws IOException {
        String command = String.format(WG_SHOW_DUMP_COMMAND, interfaceName);
        String dumpString = shell.execute(command);
        return WgShowDumpParser.fromDump(dumpString);
    }

    public String generatePrivateKey() {
        return shell.execute(WG_GENKEY_COMMAND);
    }


    public String generatePublicKey(String privateKey) {
        String command = String.format(WG_PUBKEY_COMMAND, privateKey);
        return shell.execute(command);
    }



}
