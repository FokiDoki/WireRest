package com.wireguard.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpUtilsTests {

    @Test
    void trimAll() {
        List<String> notTrimmed = List.of("\thello ", " wo rl\td", "test ");
        List<String> trimmed = Utils.trimAll(notTrimmed);
        assertEquals(notTrimmed.get(0).trim(), trimmed.get(0));
        assertEquals(notTrimmed.get(1).trim(), trimmed.get(1));
        assertEquals(notTrimmed.get(2).trim(), trimmed.get(2));
    }


}