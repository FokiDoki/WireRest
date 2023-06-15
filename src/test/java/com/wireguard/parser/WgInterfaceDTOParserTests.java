package com.wireguard.parser;

import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WgInterfaceDTOParserTests {

    private final String validbase64 = "ZHdjdmRlZXd2ZXd2ZXd2ZXd2ZXdibGVyYm1yLGJlcg==";
    @Test
    void emptyStringParsingTest() {
        String emptyString = "";
        assertThrows(IllegalArgumentException.class,
                () -> WgInterfaceParser.parse(emptyString, "\n"));
    }

    @Test
    void overloadedStringParsingTest() {
        String overloadedString = "1 2 3 4 5 6 7 8 9 10 11 12 13 14";
        assertThrows(IllegalArgumentException.class,
                () -> WgInterfaceParser.parse(overloadedString, "\n"));
    }

    @Test
    void emptyListParsingTest() {
        assertThrows(IllegalArgumentException.class,
                () -> WgInterfaceParser.parse(List.of()));
    }

    @Test
    void noPortParsingTest(){
        String noPort = "%s\t%s\t \t1234\n".formatted(validbase64,validbase64);
        assertThrows(IllegalArgumentException.class, () -> WgInterfaceParser.parse(noPort, "\t"));
    }

    @Test
    void validStringParsingTest(){
        String validString =   "%s\t%s\t1234\t321  ".formatted(validbase64,validbase64);
        WgInterfaceDTO wgInterfaceDTO = WgInterfaceParser.parse(validString, "\t");
        assertEquals(wgInterfaceDTO.getPrivateKey(), validbase64);
        assertEquals(wgInterfaceDTO.getPublicKey(), validbase64);
        assertEquals(wgInterfaceDTO.getListenPort(), 1234);
        assertEquals(wgInterfaceDTO.getFwmark(), 321);
    }

    @Test
    void validStringWithOffFwmark(){
        String validString =   "%s\t%s\t64444\toff  ".formatted(validbase64,validbase64);
        WgInterfaceDTO wgInterfaceDTO = WgInterfaceParser.parse(validString, "\t");
        assertEquals(wgInterfaceDTO.getPrivateKey(), validbase64);
        assertEquals(wgInterfaceDTO.getPublicKey(), validbase64);
        assertEquals(wgInterfaceDTO.getListenPort(), 64444);
        assertEquals(wgInterfaceDTO.getFwmark(), 0);
    }


    @Test
    void testParse() {
    }
}