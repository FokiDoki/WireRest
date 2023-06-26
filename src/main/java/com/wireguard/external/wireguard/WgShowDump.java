package com.wireguard.external.wireguard;


import com.wireguard.external.wireguard.dto.WgInterfaceDTO;

import java.util.Set;

public record WgShowDump(WgInterfaceDTO wgInterfaceDTO,
                         Set<WgPeer> peers) {
}
