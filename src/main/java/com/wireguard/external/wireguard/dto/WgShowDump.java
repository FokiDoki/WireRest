package com.wireguard.external.wireguard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WgShowDump {
    private final WgInterface wgInterface;
    private List<WgPeer> peers;


}
