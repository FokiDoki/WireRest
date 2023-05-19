package com.wireguard.external;


import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.shell.ProcessStartException;
import com.wireguard.external.shell.ShellRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@EnabledIfSystemProperty(named = "os.name", matches = "Windows.*")
public class ShellRunnerWindowsTests {
    private ShellRunner shellRunner = new ShellRunner();
    @Test
    public void run() {
        String result = shellRunner.execute("cmd.exe /c echo hello");
        Assertions.assertEquals("hello\r\n", result);
    }

    @Test
    public void runWhenExeDoesNotExists(){
        assertThatExceptionOfType(ProcessStartException.class).isThrownBy(
                () -> shellRunner.execute("imnotexists echo hello")
        );
    }

    @Test
    public void runWhenExitCodeIsNotZero(){
        assertThatExceptionOfType(CommandExecutionException.class).isThrownBy(
                () -> shellRunner.execute("cmd.exe /c exit 1")
        );
    }


}
