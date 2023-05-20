package com.wireguard.parser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class UtilsTests {

    @Test
    void trimAll() {
        List<String> notTrimmed = List.of("\thello ", " wo rl\td", "test ");
        List<String> trimmed = Utils.trimAll(notTrimmed);
        assertEquals(notTrimmed.get(0).trim(), trimmed.get(0));
        assertEquals(notTrimmed.get(1).trim(), trimmed.get(1));
        assertEquals(notTrimmed.get(2).trim(), trimmed.get(2));
    }


    @Test
    void isBase64() {
            assertTrue(Utils.isBase64("ZHdjdmRlZXd2ZXd2ZXd2ZXd2ZXdibGVyYm1yLGJlcg=="));
            assertFalse(Utils.isBase64("not-base64"));
    }


    @Test
    void throwIfNotBase64() {
        assertThrows(NotABase64Exception.class, () -> Utils.throwIfNotBase64("not-base64"));
    }
}