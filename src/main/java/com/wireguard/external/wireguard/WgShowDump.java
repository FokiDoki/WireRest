package com.wireguard.external.wireguard;


import com.wireguard.api.inteface.WgInterfaceDTO;
import com.wireguard.external.wireguard.iface.WgInterface;
import com.wireguard.external.wireguard.peer.WgPeer;

import java.util.List;

public record WgShowDump(WgInterface wgInterface,
                         List<WgPeer> peers) {
}
