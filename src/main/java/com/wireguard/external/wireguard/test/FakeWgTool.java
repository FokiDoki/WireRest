package com.wireguard.external.wireguard.test;

import com.wireguard.external.network.Subnet;
import com.wireguard.parser.WgShowDump;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.external.wireguard.iface.WgInterface;
import com.wireguard.external.wireguard.peer.WgPeer;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Profile("test")
@Component
public class FakeWgTool extends WgTool {
    private List<WgPeer> peers = new ArrayList<>();
    private WgInterface wgInterface;
    private int keyCounter;

    public FakeWgTool() {
        super(1);
        wgInterface = new WgInterface("privkey", "pubkey", 16666, 0);
        peers.addAll(List.of(
                WgPeer.publicKey("pubkey1").build(),
                WgPeer.publicKey("pubkey2").presharedKey("presharedKey2").build(),
                WgPeer.publicKey("PubKey3").presharedKey("presharedKey3").endpoint("10.0.0.1").build(),
                WgPeer.publicKey("PubKey4").presharedKey("presharedKey4").allowedIPv4Subnets(Set.of(Subnet.valueOf("10.0.0.2/32"))).build(),
                WgPeer.publicKey("PubKey5").presharedKey("presharedKey5").allowedIPv4Subnets(Set.of(Subnet.valueOf("10.0.0.3/32"))).build(),
                WgPeer.publicKey("PubKey6").presharedKey("presharedKey6").allowedIPv4Subnets(
                        Set.of(Subnet.valueOf("10.0.0.4/32"),Subnet.valueOf("10.0.0.5/32"))).build(),
                WgPeer.publicKey("PubKey7").presharedKey("presharedKey7").allowedIPv4Subnets(Set.of(Subnet.valueOf("10.0.0.6/32")))
                        .latestHandshake(100000).transferRx(12345).transferTx(54321).build(),
                WgPeer.publicKey("PubKey8").presharedKey("presharedKey8").allowedIPv4Subnets(Set.of(Subnet.valueOf("10.0.0.7/32")))
                        .latestHandshake(200000).transferRx(12345).transferTx(54321).build()
        ));
        keyCounter = peers.size()+1;
    }
    @SneakyThrows
    @Override
    public WgShowDump showDump(String interfaceName) {
        return new WgShowDump(wgInterface, peers);
    }

    @Override
    public String generatePrivateKey() {
        return "PrivateKey"+keyCounter++;
    }

    @Override
    synchronized public void addPeer(String interfaceName, WgPeer peer) {
        peers.add(WgPeer.publicKey(peer.getPublicKey())
                .presharedKey(peer.getPresharedKey())
                .allowedIPv4Subnets(peer.getAllowedSubnets().getIPv4Subnets())
                .persistentKeepalive(peer.getPersistentKeepalive()).build()
        );
    }

    @Override
    public void deletePeer(String interfaceName, String publicKey) {
        peers.stream().filter(peer -> peer.getPublicKey().equals(publicKey)).findFirst().ifPresent(peers::remove);
    }




    @Override
    public String generatePublicKey(String privateKey) {
        return "PubKey"+keyCounter++;
    }

    @Override
    public String generatePresharedKey() {
        return "preSharedKey"+keyCounter++;
    }


}
