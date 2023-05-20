package com.wireguard.parser;

import com.wireguard.external.wireguard.WgPeer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WgPeerParserTest {
    
    @Test
    public void testParseEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WgPeerParser.parse(List.of());
        });
        Assertions.assertThat(exception.getMessage()).isEqualTo("WgPeerParser.parse: invalid number of arguments in [], 8 expected");
    }
    @Test
    public void testParse() {
        String pubkey = "Ds123123312G859AO3s1I8vhTgrgrgrgrgrgt9LKF8B=";
        String presharedkey = "Ds123123312G859AO3s1I8vhTgrgrgrgrgrgt9LKF8B=";
        List<String> data = List.of(pubkey,
                presharedkey, "  90.90.90.90:2222  ",
                "10.66.66.10/24", "12345678  ", "2222", "1111 ", "  off  ");
        int dataHash = data.hashCode();
        WgPeer wgPeer = WgPeerParser.parse(data);
        Assertions.assertThat(data.hashCode()).isEqualTo(dataHash);
        Assertions.assertThat(wgPeer.getPublicKey()).isEqualTo(pubkey);
        Assertions.assertThat(wgPeer.getPresharedKey()).isEqualTo(presharedkey);
        Assertions.assertThat(wgPeer.getEndpoint()).isEqualTo("90.90.90.90:2222");
        Assertions.assertThat(wgPeer.getAllowedIps()).isEqualTo("10.66.66.10/24");
        Assertions.assertThat(wgPeer.getLatestHandshake()).isEqualTo(12345678L);
        Assertions.assertThat(wgPeer.getTransferRx()).isEqualTo(2222);
        Assertions.assertThat(wgPeer.getTransferTx()).isEqualTo(1111);
        Assertions.assertThat(wgPeer.getPersistentKeepalive()).isEqualTo(0);
    }
}
