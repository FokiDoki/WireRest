package com.wirerest.api.converters;

import com.wirerest.api.dto.WgKey;
import com.wirerest.api.peer.PeerUpdateRequestDTO;
import com.wirerest.network.ISubnet;
import com.wirerest.utils.IpUtils;
import com.wirerest.wireguard.peer.requests.PeerUpdateRequest;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;
import java.util.Set;

public class PeerUpdateRequestFromDTOConverter implements Converter<PeerUpdateRequestDTO, PeerUpdateRequest> {
    @Override
    public PeerUpdateRequest convert(PeerUpdateRequestDTO dto) {
        Optional<Set<ISubnet>> allowedIps = Optional.empty();
        if (dto.getAllowedIps() != null) {
            allowedIps = Optional.of(IpUtils.stringToSubnetSet(dto.getAllowedIps()));
        }
        return new PeerUpdateRequest(
                getWgKey(dto.getPublicKey()),
                getWgKey(dto.getNewPublicKey()),
                getWgKey(dto.getPresharedKey()),
                allowedIps.orElse(null),
                getEndpoint(dto),
                dto.getPersistentKeepalive()
        );
    }

    private String getEndpoint(PeerUpdateRequestDTO dto) {
        if (dto.getEndpoint() == null) return null;
        return "%s:%d".formatted(dto.getEndpoint().getHost(), dto.getEndpoint().getPort());
    }

    private String getWgKey(WgKey key) {
        if (key == null) return null;
        return key.getValue();
    }
}
