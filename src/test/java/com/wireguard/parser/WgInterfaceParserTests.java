package com.wireguard.parser;

import com.wireguard.external.wireguard.WgInterface;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WgInterfaceParserTests {

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
    void invalidBase64ParsingTest(){
        String invalid2keys = "1234567890\t1234567890\t1234\t1234\n";
        assertThrows(NotABase64Exception.class, () -> WgInterfaceParser.parse(invalid2keys, "\t"));
        String validFirstBase64ButInvalidTwo = "%s\t1234567890\t1234\t1234\n".formatted(validbase64);
        assertThrows(NotABase64Exception.class, () -> WgInterfaceParser.parse(validFirstBase64ButInvalidTwo, "\t"));
    }

    @Test
    void invalidPortParsingTest(){
        String invalidPort = "%s\t%s\t0\t1234\n".formatted(validbase64,validbase64);
        assertThrows(IllegalArgumentException.class, () -> WgInterfaceParser.parse(invalidPort, "\t"));
    }

    @Test
    void invalidPortParsingTest2(){
        String invalidPort = "%s\t%s\t1234567890\t1234\n".formatted(validbase64,validbase64);
        assertThrows(IllegalArgumentException.class, () -> WgInterfaceParser.parse(invalidPort, "\t"));
    }

    @Test
    void noPortParsingTest(){
        String noPort = "%s\t%s\t \t1234\n".formatted(validbase64,validbase64);
        assertThrows(IllegalArgumentException.class, () -> WgInterfaceParser.parse(noPort, "\t"));
    }

    @Test
    void validStringParsingTest(){
        String validString =   "%s\t%s\t1234\t321  ".formatted(validbase64,validbase64);
        WgInterface wgInterface = WgInterfaceParser.parse(validString, "\t");
        assertEquals(wgInterface.getPrivateKey(), validbase64);
        assertEquals(wgInterface.getPublicKey(), validbase64);
        assertEquals(wgInterface.getListenPort(), 1234);
        assertEquals(wgInterface.getFwmark(), 321);
    }

    @Test
    void validStringWithOffFwmark(){
        String validString =   "%s\t%s\t64444\toff  ".formatted(validbase64,validbase64);
        WgInterface wgInterface = WgInterfaceParser.parse(validString, "\t");
        assertEquals(wgInterface.getPrivateKey(), validbase64);
        assertEquals(wgInterface.getPublicKey(), validbase64);
        assertEquals(wgInterface.getListenPort(), 64444);
        assertEquals(wgInterface.getFwmark(), 0);
    }


    @Test
    void testParse() {
    }
}