package com.wireguard.external.wireguard;


import com.wireguard.external.wireguard.dto.WgInterfaceDTO;

import java.util.List;

public record WgShowDump(WgInterfaceDTO wgInterfaceDTO,
                         List<WgPeer> peers) {
}
