package com.wireguard.external.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class ShellRunner {
    private static final Logger logger = LoggerFactory.getLogger(ShellRunner.class);
    private final Charset charset;

    public ShellRunner(Charset charset) {
        this.charset = charset;
    }

    public ShellRunner() {
        this.charset = StandardCharsets.UTF_8;
    }



    public String execute(String[] command, List<Integer> allowedExitCodes) {
        Process process = startProcess(command);
        int exitCode = waitForProcess(process);
        String stdout = readInputStream(process.getInputStream());
        String stderr = readInputStream(process.getErrorStream());
        if (!allowedExitCodes.contains(exitCode)) {
            logger.debug("Command failed with exit code %d: %s".formatted(exitCode, String.join(" ", command)));
            throw new CommandExecutionException(String.join(" ", command), exitCode, stdout, stderr);
        }
        logger.debug("Command executed successfully: %s".formatted(String.join(" ", command)));
        return stdout;
    }

    public String execute(String[] command){
        return execute(command, List.of(0));
    }

    public Process startProcess(String[] command) {
        String stringCommand = String.join(" ", command);
        logger.trace("Executing command: %s".formatted(stringCommand));
        Runtime runtime = Runtime.getRuntime();
        try {
            return runtime.exec(command);
        } catch (IOException e) {
            logger.error("Error executing command: %s".formatted(stringCommand), e);
            throw new ProcessStartException(stringCommand, e);
        }
    }

    private int waitForProcess(Process process) {
        try {
            return process.waitFor();
        }
        catch (InterruptedException ex) {
            throw new IllegalStateException("Interrupted waiting for %s".formatted(process));
        }
    }

    private String readInputStream(InputStream inputStream) {
        return new StreamToStringConverter(charset).convert(inputStream);
    }



}
