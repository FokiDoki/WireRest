package com.wirerest.wireguard.peer;

import com.wirerest.network.IV4SubnetSolver;
import com.wirerest.network.Subnet;
import com.wirerest.wireguard.peer.requests.EmptyPeerCreationRequest;
import com.wirerest.wireguard.peer.requests.IpAllocationRequestOneIfNullSubnets;
import com.wirerest.wireguard.peer.requests.PeerCreationRequest;
import com.wirerest.wireguard.WgTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WgPeerGeneratorTest {
    IV4SubnetSolver subnetSolver;
    WgTool wgTool;
    WgPeerGenerator wgPeerGenerator;

    @BeforeEach
    public void setup() {
        subnetSolver = Mockito.mock(IV4SubnetSolver.class);
        Mockito.when(subnetSolver.obtainFree(Mockito.anyInt())).thenReturn(Subnet.valueOf("0.0.0.0/32"));

        wgTool = Mockito.mock(WgTool.class);
        Mockito.when(wgTool.generatePublicKey(Mockito.any())).thenReturn("publicKey");
        wgPeerGenerator = new WgPeerGenerator(wgTool);
    }

    @Test
    public void testCreatePeerWithNulls() {
        CreatedPeer peer = wgPeerGenerator.createPeerGenerateNulls(new EmptyPeerCreationRequest());
        assertEquals(wgPeerGenerator.DEFAULT_PERSISTENT_KEEPALIVE, peer.getPersistentKeepalive());
        Mockito.verify(wgTool, Mockito.times(1)).generatePublicKey(Mockito.any());
        Mockito.verify(subnetSolver, Mockito.times(0)).obtainFree(Mockito.anyInt());
        Mockito.verify(wgTool, Mockito.times(1)).generatePrivateKey();
        Mockito.verify(wgTool, Mockito.times(1)).generatePresharedKey();
    }

    @Test
    public void testCreatePeerWithData() {
        CreatedPeer peer = wgPeerGenerator.createPeerGenerateNulls(new PeerCreationRequest("publicKey", "presharedKey",
                "privateKey", new IpAllocationRequestOneIfNullSubnets(Set.of(Subnet.valueOf("0.0.0.1/32"))), 1));
        assertEquals(1, peer.getPersistentKeepalive());
        assertEquals("publicKey", peer.getPublicKey());
        assertEquals("presharedKey", peer.getPresharedKey());
        assertEquals("privateKey", peer.getPrivateKey());
        assertEquals(Set.of(Subnet.valueOf("0.0.0.1/32")), peer.getAllowedSubnets());
        Mockito.verify(wgTool, Mockito.times(0)).generatePublicKey(Mockito.any());
        Mockito.verify(wgTool, Mockito.times(0)).generatePrivateKey();
        Mockito.verify(wgTool, Mockito.times(0)).generatePresharedKey();
    }

}