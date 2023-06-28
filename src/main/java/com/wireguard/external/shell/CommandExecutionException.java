package com.wireguard.external.shell;

import lombok.Data;

@Data
public class CommandExecutionException extends RuntimeException {
    private final String stderr;
    private final String stdout;
    public CommandExecutionException(String command, int exitCode, String stdout, String stderr) {
        super("Error executing command: %s, exit code: %d, stdout: %s, stderr: %s".formatted(
                command,
                exitCode,
                stdout,
                stderr));
        this.stderr = stderr;
        this.stdout = stdout;
    }
}
