package com.wireguard.external.wireguard;

import com.wireguard.external.shell.ShellRunner;
import com.wireguard.external.wireguard.peer.WgPeer;
import com.wireguard.external.wireguard.tools.RateLimitedExecutorService;
import com.wireguard.parser.WgShowDumpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

@Profile("prod")
@Component
public class WgTool {
    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);

    private static final String WG_SHOW_DUMP_COMMAND = "wg show %s dump";
    private static final String WG_GENKEY_COMMAND = "wg genkey";
    private static final String WG_PUBKEY_COMMAND =  "echo %s | wg pubkey";
    private static final String WG_PRESHARED_KEY_COMMAND = "wg genpsk";
    private static final String WG_ADD_PEER_COMMAND = "wg set %s peer %s";
    private static final String CREATE_FILE_COMMAND = "echo %s > %s";
    private static final String DELETE_FILE_COMMAND = "rm %s";
    private static final String WG_DEL_PEER_COMMAND = "wg set %s peer %s remove";
    private static final String WG_SAVE_COMMAND = "wg-quick save %s";
    private static final String WG_SHOW_CONF_COMMAND = "wg showconf %s";

    static String presharedKeyPath = "/tmp/";
    protected final ShellRunner shell = new ShellRunner();

    private final Queue<Task> configSaveTasks = new LinkedBlockingQueue<>(1);
    private final RateLimitedExecutorService configSaveExecutor;

    @Autowired
    public WgTool(@Value("${wg.config.save.min-interval}") int configSaveMinInterval) {
        this.configSaveExecutor = new RateLimitedExecutorService(configSaveTasks, configSaveMinInterval);
    }

    private String run(String commandStr, Boolean privileged) {
        String[] command = getCommand(commandStr, privileged);
        logger.debug("Running command: %s".formatted(String.join(" ", command)));
        return shell.execute(command).strip();
    }
    protected String[] getCommand(String commandStr, Boolean privileged) {
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



    public WgShowDump showDump(String interfaceName) {
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
    public void addPeer(String interfaceName, WgPeer peer) {
        StringBuilder command = new StringBuilder();
        String filePath = presharedKeyPath+getRandomFileName();
        command.append(WG_ADD_PEER_COMMAND.formatted(
                interfaceName,
                peer.getPublicKey()));
        if (!peer.getPresharedKey().isEmpty())
            createFile(filePath, peer.getPresharedKey());
        List<Argument> arguments = List.of(
                new Argument("preshared-key",
                        peer.getPresharedKey().isEmpty() ? null : filePath),
                new Argument("allowed-ips", peer.getAllowedSubnets().toString()),
                new Argument("persistent-keepalive", String.valueOf(peer.getPersistentKeepalive()))
        );
        appendArgumentsIfPresentAndNotEmpty(arguments, command);

        try{
            run(command.toString(), true);
        } finally {
            if (!peer.getPresharedKey().isEmpty())
                deleteFile(filePath);
        }
        saveConfig(interfaceName);
    }

    private String getRandomFileName() {
        return UUID.randomUUID().toString();
    }

    private static class Argument{
        private final String name;
        private final String value;
        private final Function<String, String> valueTransformer;

        public Argument(String name, String value, Function<String, String> valueTransformer) {
            this.name = name;
            this.value = value;
            this.valueTransformer = valueTransformer;
        }

        public Argument(String name, String value) {
            this(name, value, (v) -> v);
        }

        public boolean isPresent(){
            return value != null && !value.isEmpty();
        }

        public String getCommand(){
            return String.format(" %s %s", name, valueTransformer.apply(value));
        }
    }

    private void appendArgumentsIfPresentAndNotEmpty(List<Argument> arguments, StringBuilder command) {
        for (Argument argument: arguments) {
            if (argument.isPresent()) {
                command.append(argument.getCommand());
            }
        }

    }
    private void saveConfig(String interfaceName) {
        if (configSaveTasks.isEmpty()) {
            logger.debug("Scheduling config save for interface %s".formatted(interfaceName));
            configSaveTasks.add(new Task(() ->
            {
                run(WG_SAVE_COMMAND.formatted(interfaceName), true);
                logger.debug("Config saved for interface %s".formatted(interfaceName));
            }));
        }
    }



    public void deletePeer(String interfaceName, String publicKey) {
        run(WG_DEL_PEER_COMMAND.formatted(interfaceName, publicKey), true);
        saveConfig(interfaceName);
    }


}
