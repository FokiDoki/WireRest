package com.wireguard.parser;

import com.wireguard.external.wireguard.WgPeer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WgPeerParserTest {
    private List<String> data;

    @BeforeEach
    public void setUp() {
        data = List.of(
                "Ds123123312G859AO3s1I8vhTgrgrgrgrgrgt9LKF8B=",
                "Ds123123312G859AO3s1I8vhTgrgrgrgrgrgt9LKF8B=",
                "  90.90.90.90:2222  ",
                "10.66.66.10/24",
                "12345678  ",
                "2222",
                "1111 ",
                "  off  ");
    }

    @Test
    public void testParseEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WgPeerParser.parse(List.of());
        });
        Assertions.assertThat(exception.getMessage()).isEqualTo("WgPeerParser.parse: invalid number of arguments in [], 8 expected");
    }
    @Test
    public void testParse() {
        int dataHash = data.hashCode();
        WgPeer wgPeer = WgPeerParser.parse(data);
        Assertions.assertThat(data.hashCode()).isEqualTo(dataHash);
        Assertions.assertThat(wgPeer.getPublicKey()).isEqualTo(data.get(0));
        Assertions.assertThat(wgPeer.getPresharedKey()).isEqualTo(data.get(1));
        Assertions.assertThat(wgPeer.getEndpoint()).isEqualTo("90.90.90.90:2222");
        Assertions.assertThat(wgPeer.getAllowedIps().toString()).isEqualTo("10.66.66.10/24");
        Assertions.assertThat(wgPeer.getLatestHandshake()).isEqualTo(12345678L);
        Assertions.assertThat(wgPeer.getTransferRx()).isEqualTo(2222);
        Assertions.assertThat(wgPeer.getTransferTx()).isEqualTo(1111);
        Assertions.assertThat(wgPeer.getPersistentKeepalive()).isEqualTo(0);
    }

    @Test
    void invalidIpTest(){
        List<String> invalidIpsData = new ArrayList<>(data);
        invalidIpsData.set(3, "invalidIp");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WgPeerParser.parse(invalidIpsData);
        });
    }

    @Test
    void invalidPersistenceKeepaliveData(){
        List<String> invalidPersistenceKeepaliveData = new ArrayList<>(data);
        invalidPersistenceKeepaliveData.set(7, "invalid");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WgPeerParser.parse(invalidPersistenceKeepaliveData);
        });
    }
}
