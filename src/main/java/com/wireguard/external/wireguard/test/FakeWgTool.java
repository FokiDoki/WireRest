package com.wireguard.external.wireguard.test;

import com.wireguard.converters.StreamToStringConverter;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.external.wireguard.WgShowDump;
import com.wireguard.parser.WgShowDumpParser;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.stream.Stream;

@Profile("test")
@Component
public class FakeWgTool extends WgTool {

    private final static StreamToStringConverter streamToStringConverter = new StreamToStringConverter();
    @SneakyThrows
    @Override
    public WgShowDump showDump(String interfaceName) {
        File wgShowDumpFile = new File("src/main/resources/test-data/wg_show_dump.txt");
        String dump = streamToStringConverter.convert(new FileInputStream(wgShowDumpFile));
        return WgShowDumpParser.fromDump(dump);
    }

    @Override
    public String generatePrivateKey() {
        return null;
    }

    @Override
    public String generatePublicKey(String privateKey) {
        return null;
    }
}
