package com.wireguard.external;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class UbuntuCommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(UbuntuCommandExecutor.class);
    public static Charset charset = StandardCharsets.UTF_8;

    public static ExecutionResult execute(String command) {
        logger.info("Executing command: " + command);
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            process.waitFor();
            return new ExecutionResult(
                    process.getInputStream(),
                    process.getErrorStream(),
                    process.exitValue());
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing command: " + command, e);
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class ExecutionResult {
        private InputStream inputStream;
        private InputStream errorStream;
        private int exitCode;

        protected ExecutionResult(InputStream output, InputStream error, int exitCode) {
            this.inputStream = output;
            this.errorStream = error;
            this.exitCode = exitCode;
        }
    }
}
