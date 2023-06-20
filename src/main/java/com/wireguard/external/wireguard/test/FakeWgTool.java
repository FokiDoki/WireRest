package com.wireguard.external.wireguard.test;

import com.wireguard.converters.StreamToStringConverter;
import com.wireguard.external.wireguard.WgShowDump;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.parser.WgShowDumpParser;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.stream.Stream;

@Profile("test")
@Component
public class FakeWgTool extends WgTool {

    private final static StreamToStringConverter streamToStringConverter = new StreamToStringConverter();
    @SneakyThrows
    @Override
    public WgShowDump showDump(String interfaceName) {
        File wgShowDumpFile = new File("src/main/resources/test-data/wg_show_dump.txt");
        Scanner dump = new Scanner(new FileInputStream(wgShowDumpFile));
        return WgShowDumpParser.fromDump(dump);
    }

    @Override
    public String generatePrivateKey() {
        return "FAKEprv/"+generateRandomString(35)+"=\\r\\n";
    }

    private String generateRandomString(int length) {
        return Stream.generate(() -> "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
                .limit(length)
                .mapToInt(n -> (int) (n.charAt((int) (Math.random() * n.length()))))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    @Override
    public String generatePublicKey(String privateKey) {
        return "FAKEpub/"+generateRandomString(35)+"=\\r\\n";
    }

    @Override
    public String generatePresharedKey() {
        return "FAKEpsk/"+generateRandomString(35)+"=\\r\\n";
    }

}
