package com.wireguard.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class WgShowDump {
    private final WgInterface wgInterface;
    private final List<WgPeer> peers;


}
