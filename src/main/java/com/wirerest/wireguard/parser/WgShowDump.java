package com.wirerest.wireguard.parser;


import com.wirerest.wireguard.iface.WgInterface;
import com.wirerest.wireguard.peer.WgPeer;

import java.util.List;

public record WgShowDump(WgInterface wgInterface,
                         List<WgPeer> peers) {
}
