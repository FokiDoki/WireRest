package com.wireguard.external.wireguard.peer;

import com.wireguard.external.network.ISubnetSolver;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.wireguard.WgTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WgPeerCreatorTest {
    ISubnetSolver subnetSolver;
    WgTool wgTool;
    WgPeerCreator wgPeerCreator;

    @BeforeEach
    public void setup() {
        subnetSolver = Mockito.mock(ISubnetSolver.class);
        Mockito.when(subnetSolver.obtainFree(Mockito.anyInt())).thenReturn(Subnet.valueOf("0.0.0.0/32"));

        wgTool = Mockito.mock(WgTool.class);
        Mockito.when(wgTool.generatePublicKey(Mockito.any())).thenReturn("publicKey");
        wgPeerCreator = new WgPeerCreator(wgTool,subnetSolver);
    }

    @Test
    public void testCreatePeerWithNulls(){
         CreatedPeer peer = wgPeerCreator.createPeerGenerateNulls(null,null,
                null,null,null);
            assertEquals(wgPeerCreator.DEFAULT_PERSISTENT_KEEPALIVE, peer.getPersistentKeepalive());
            Mockito.verify(wgTool, Mockito.times(1)).generatePublicKey(Mockito.any());
            Mockito.verify(subnetSolver, Mockito.times(1)).obtainFree(Mockito.anyInt());
            Mockito.verify(wgTool, Mockito.times(1)).generatePrivateKey();
            Mockito.verify(wgTool, Mockito.times(1)).generatePresharedKey();
    }

    @Test
    public void testCreatePeerWithData(){
        CreatedPeer peer = wgPeerCreator.createPeerGenerateNulls("publicKey","presharedKey",
                "privateKey", Set.of(Subnet.valueOf("0.0.0.1/32")),1);
        assertEquals(1, peer.getPersistentKeepalive());
        assertEquals("publicKey", peer.getPublicKey());
        assertEquals("presharedKey", peer.getPresharedKey());
        assertEquals("privateKey", peer.getPrivateKey());
        assertEquals(Set.of(Subnet.valueOf("0.0.0.1/32")), peer.getAllowedSubnets());
        Mockito.verify(wgTool, Mockito.times(0)).generatePublicKey(Mockito.any());
        Mockito.verify(subnetSolver, Mockito.times(0)).obtainFree(Mockito.anyInt());
        Mockito.verify(wgTool, Mockito.times(0)).generatePrivateKey();
        Mockito.verify(wgTool, Mockito.times(0)).generatePresharedKey();
        Mockito.verify(subnetSolver, Mockito.times(1)).obtain(Subnet.valueOf("0.0.0.1/32"));
    }

    @Test
    public void testCreatePeerWithExceptionSubnetSolver(){
        Mockito.doThrow(new RuntimeException()).when(subnetSolver).obtain(Mockito.any());
        Set<Subnet> ips = Set.of(Subnet.valueOf("10.0.0.1/32"), Subnet.valueOf("10.0.0.2/32"));
        Assertions.assertThrows(RuntimeException.class, () -> {
            wgPeerCreator.createPeerGenerateNulls(null,null,
                    null,ips,null);
        });
        Mockito.verify(subnetSolver, Mockito.times(2)).release(Mockito.any(Subnet.class));
    }

}