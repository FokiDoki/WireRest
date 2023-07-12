package com.wireguard.external.wireguard.test;

import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.WgPeerContainer;
import com.wireguard.external.wireguard.WgShowDump;
import com.wireguard.external.wireguard.WgTool;
import com.wireguard.external.wireguard.dto.CreatedPeer;
import com.wireguard.external.wireguard.dto.WgInterfaceDTO;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Profile("test")
@Component
public class FakeWgTool extends WgTool {
    private WgPeerContainer wgPeerContainer = new WgPeerContainer();
    private WgInterfaceDTO wgInterfaceDTO;
    private int keyCounter;

    public FakeWgTool() {
        super(1);
        wgInterfaceDTO = new WgInterfaceDTO("privkey", "pubkey", 16666, 0);
        wgPeerContainer.addAll(List.of(
                WgPeer.publicKey("pubkey1").build(),
                WgPeer.publicKey("pubkey2").presharedKey("presharedKey2").build(),
                WgPeer.publicKey("PubKey3").presharedKey("presharedKey3").endpoint("10.0.0.1").build(),
                WgPeer.publicKey("PubKey4").presharedKey("presharedKey4").allowedIPv4Ips(Set.of("10.0.0.2/32")).build(),
                WgPeer.publicKey("PubKey5").presharedKey("presharedKey5").allowedIPv4Ips(Set.of("10.0.0.3/32")).build(),
                WgPeer.publicKey("PubKey6").presharedKey("presharedKey6").allowedIPv4Ips(Set.of("10.0.0.4/32","10.0.0.5/32")).build(),
                WgPeer.publicKey("PubKey7").presharedKey("presharedKey7").allowedIPv4Ips(Set.of("10.0.0.6/32"))
                        .latestHandshake(100000).transferRx(12345).transferTx(54321).build(),
                WgPeer.publicKey("PubKey8").presharedKey("presharedKey8").allowedIPv4Ips(Set.of("10.0.0.7/32"))
                        .latestHandshake(200000).transferRx(12345).transferTx(54321).build()
        ));
        keyCounter = wgPeerContainer.size();

    }
    @SneakyThrows
    @Override
    public WgShowDump showDump(String interfaceName) {
        return new WgShowDump(wgInterfaceDTO, wgPeerContainer);
    }

    @Override
    public String generatePrivateKey() {
        return "PrivateKey"+keyCounter++;
    }

    @Override
    synchronized public void addPeer(String interfaceName, CreatedPeer peer) {
        wgPeerContainer.add(WgPeer.publicKey(peer.getPublicKey())
                .presharedKey(peer.getPresharedKey())
                .allowedIPv4Ips(peer.getAddress())
                .persistentKeepalive(peer.getPersistentKeepalive()).build()
        );
    }

    @Override
    public void deletePeer(String interfaceName, String publicKey) {
        wgPeerContainer.removePeerByPublicKey(publicKey);
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
