package com.wirerest.api.converters;

import com.wirerest.api.dto.WgKey;
import com.wirerest.api.peer.PeerCreationRequestDTO;
import com.wirerest.utils.IpUtils;
import com.wirerest.wireguard.peer.requests.IpAllocationRequestOneIfNullSubnets;
import com.wirerest.wireguard.peer.requests.PeerCreationRequest;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class PeerCreationRequestFromDTOConverter implements Converter<PeerCreationRequestDTO, PeerCreationRequest> {

    @Override
    public PeerCreationRequest convert(PeerCreationRequestDTO dto) {
        return new PeerCreationRequest(
                Objects.requireNonNullElse(dto.getPublicKey(), new WgKey(null)).getValue(),
                Objects.requireNonNullElse(dto.getPresharedKey(), new WgKey(null)).getValue(),
                Objects.requireNonNullElse(dto.getPrivateKey(), new WgKey(null)).getValue(),
                new IpAllocationRequestOneIfNullSubnets(
                        Optional.ofNullable(dto.getAllowedIps())
                                .map(allowedIps -> IpUtils.stringToSubnetSet(new HashSet<>(allowedIps)))
                                .orElse(null)
                ),
                dto.getPersistentKeepalive()
        );
    }
}
