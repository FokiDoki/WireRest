package com.wireguard.parser;


import com.wireguard.external.wireguard.iface.WgInterface;
import com.wireguard.external.wireguard.peer.WgPeer;

import java.util.List;

public record WgShowDump(WgInterface wgInterface,
                         List<WgPeer> peers) {
}
