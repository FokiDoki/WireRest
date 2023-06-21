package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.parser.WgShowDumpParser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Scanner;

@Profile("prod")
@Component
public class WgTool {

    private static final String WG_SHOW_DUMP_COMMAND = "wg show %s dump";
    private static final String WG_GENKEY_COMMAND = "wg genkey";
    private static final String WG_PUBKEY_COMMAND =  "echo %s | wg pubkey";
    private static final String WG_PRESHARED_KEY_COMMAND = "wg genpsk";
    private static final String WG_ADD_PEER_COMMAND = "wg set %s peer %s preshared-key %s allowed-ips %s persistent-keepalive %d";
    private static final String CREATE_FILE_COMMAND = "echo %s > %s";
    private static final String DELETE_FILE_COMMAND = "rm %s";
    private static final String WG_SAVE_COMMAND = "wg-quick save %s";
    private static final String WG_SHOW_CONF_COMMAND = "wg showconf %s";
    private static final String presharedKeyPath = "/tmp/presharedKey";
    protected final ShellRunner shell = new ShellRunner();

    private String run(String commandStr, Boolean privileged) {
        String[] command = getCommand(commandStr, privileged);
        return shell.execute(command).strip();
    }
    private String[] getCommand(String commandStr, Boolean privileged) {
        String[] command;
        if (privileged) {
            command = new String[]{"sudo", "/bin/sh", "-c", commandStr};
        } else {
            command = new String[]{"/bin/sh", "-c", commandStr};
        }
        return command;
    }

    private String run(String commandStr) {
        return run(commandStr, false);
    }

    private Scanner runToScanner(String commandStr, Boolean privileged) {
        String[] command = getCommand(commandStr, privileged);
        Process process = shell.startProcess(command);
        return new Scanner(process.getInputStream());
    }



    public WgShowDump showDump(String interfaceName) throws IOException {
        Scanner scanner = runToScanner(WG_SHOW_DUMP_COMMAND.formatted(interfaceName), true);
        return WgShowDumpParser.fromDump(scanner);
    }

    public String showConf(String interfaceName) {
        Scanner scanner = runToScanner(WG_SHOW_CONF_COMMAND.formatted(interfaceName), true);
        StringBuilder conf = new StringBuilder();
        while (scanner.hasNextLine()) {
            conf.append(scanner.nextLine()).append("\n");
        }
        return conf.toString();
    }

    public String generatePrivateKey() {
        return run(WG_GENKEY_COMMAND);
    }


    public String generatePublicKey(String privateKey) {
        return run(WG_PUBKEY_COMMAND.formatted(privateKey));
    }

    private void createFile(String path, String content) {
        run(CREATE_FILE_COMMAND.formatted(content, path));
    }

    private void deleteFile(String path) {
        run(DELETE_FILE_COMMAND.formatted(path));
    }

    public String generatePresharedKey() {
        return run(WG_PRESHARED_KEY_COMMAND);
    }

    //I don't know how to do this without creating a file (wg set doesn't accept preshared key as a parameter)
    public void addPeer(String interfaceName, String publicKey, String presharedKey,
                        String allowedIps, int persistentKeepalive) {
        createFile(presharedKeyPath, presharedKey);
        try{
            run(WG_ADD_PEER_COMMAND.formatted(
                interfaceName, publicKey, presharedKeyPath, allowedIps, persistentKeepalive), true);
        } finally {
            deleteFile(presharedKeyPath);
        }
        saveConfig(interfaceName);
    }

    private void saveConfig(String interfaceName) {
        run(WG_SAVE_COMMAND.formatted(interfaceName), true);
    }



}
