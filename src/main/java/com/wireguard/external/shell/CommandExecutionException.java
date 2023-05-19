package com.wireguard.external.shell;

public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException(String command, int exitCode, String stdout, String stderr) {
        super("Error executing command: %s, exit code: %d, stdout: %s, stderr: %s".formatted(
                command,
                exitCode,
                stdout,
                stderr));
    }
}
