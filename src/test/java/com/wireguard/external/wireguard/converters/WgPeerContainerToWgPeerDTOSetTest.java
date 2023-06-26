package com.wireguard.external.wireguard.converters;

import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.WgPeerContainer;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WgPeerContainerToWgPeerDTOSetTest {
        WgPeerContainerToWgPeerDTOSet converter = new WgPeerContainerToWgPeerDTOSet();
        @Test
        void convert() {
            WgPeerContainer container = new WgPeerContainer();
            container.add(WgPeer.publicKey("1").build());
            container.add(WgPeer.publicKey("2").build());
            Set<WgPeerDTO> dtoset = converter.convert(container);
            assertNotNull(dtoset);
            assertFalse(dtoset.isEmpty());
            assertEquals(2, dtoset.size());
        }
}