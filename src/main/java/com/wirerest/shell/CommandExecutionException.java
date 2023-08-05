package com.wirerest.shell;

import lombok.Data;

@Data
public class CommandExecutionException extends RuntimeException {
    private final String stderr;
    private final String stdout;
    private final int exitCode;

    public CommandExecutionException(String command, int exitCode, String stdout, String stderr) {
        super("Error executing command: %s, exit code: %d, stdout: %s, stderr: %s".formatted(
                command,
                exitCode,
                stdout,
                stderr));
        this.exitCode = exitCode;
        this.stderr = stderr;
        this.stdout = stdout;
    }
}
