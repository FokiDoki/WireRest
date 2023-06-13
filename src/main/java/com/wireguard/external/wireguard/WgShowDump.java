package com.wireguard.external.wireguard;


import java.util.List;

public record WgShowDump(WgInterface wgInterface,
                         List<WgPeer> peers) {
}
