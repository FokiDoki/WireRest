package com.wireguard.external;


import com.wireguard.external.UbuntuCommandExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


public class UbuntuCommandExecutorWindowsTest {
    @Test
    public void testExecute() throws IOException {
        UbuntuCommandExecutor.ExecutionResult result = UbuntuCommandExecutor.execute("cmd /c ECHO HELLO WORLD");
        Assertions.assertEquals("HELLO WORLD\r\n", streamToString(result.getInputStream()));
        Assertions.assertEquals("", streamToString(result.getErrorStream()));
        Assertions.assertEquals(0, result.getExitCode());
    }

    @Test
    public void wrongCommand() throws IOException {
        UbuntuCommandExecutor.ExecutionResult result = UbuntuCommandExecutor.execute("cmd /c TACO HELLO WORLD");
        Assertions.assertEquals("", streamToString(result.getInputStream()));
        Assertions.assertNotEquals("", streamToString(result.getErrorStream()));
        Assertions.assertEquals(1, result.getExitCode());
    }


    private String streamToString(InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), Charset.forName("utf-8"));
    }
}
