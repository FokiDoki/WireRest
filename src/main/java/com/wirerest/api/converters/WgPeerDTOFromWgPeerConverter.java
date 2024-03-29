package com.wirerest.api.converters;

import com.wirerest.api.peer.WgPeerDTO;
import com.wirerest.wireguard.peer.WgPeer;
import org.springframework.core.convert.converter.Converter;

public class WgPeerDTOFromWgPeerConverter implements Converter<WgPeer, WgPeerDTO> {
    @Override
    public WgPeerDTO convert(WgPeer peer) {
        WgPeerDTO wgPeerDTO = new WgPeerDTO();
        wgPeerDTO.setPublicKey(peer.getPublicKey());
        wgPeerDTO.setPresharedKey(peer.getPresharedKey());
        wgPeerDTO.setEndpoint(peer.getEndpoint());
        wgPeerDTO.setAllowedSubnets(peer.getAllowedSubnets().getAllStrings());
        wgPeerDTO.setLatestHandshake(peer.getLatestHandshake());
        wgPeerDTO.setTransferRx(peer.getTransferRx());
        wgPeerDTO.setTransferTx(peer.getTransferTx());
        wgPeerDTO.setPersistentKeepalive(peer.getPersistentKeepalive());

        return wgPeerDTO;
    }
}
