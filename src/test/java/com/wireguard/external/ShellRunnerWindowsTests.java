package com.wireguard.external;


import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.shell.ProcessStartException;
import com.wireguard.external.shell.ShellRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@EnabledOnOs(OS.WINDOWS)
public class ShellRunnerWindowsTests {
    private ShellRunner shellRunner = new ShellRunner();
    @Test
    public void run() {
        String result = shellRunner.execute(new String[]{"cmd.exe", "/c", "echo hello"});
        Assertions.assertEquals("hello\r\n", result);
    }

    @Test
    public void runWhenExeDoesNotExists(){
        assertThatExceptionOfType(ProcessStartException.class).isThrownBy(
                () -> shellRunner.execute(new String[]{"imnotexists", "echo", "hello"})
        );
    }

    @Test
    public void runWhenExitCodeIsNotZero(){
        assertThatExceptionOfType(CommandExecutionException.class).isThrownBy(
                () -> shellRunner.execute(new String[]{"cmd.exe", "/c", "exit 1"})
        );
    }


}
