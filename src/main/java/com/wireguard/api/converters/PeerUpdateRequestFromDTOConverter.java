package com.wireguard.api.converters;

import com.wireguard.api.dto.WgKey;
import com.wireguard.api.peer.PeerUpdateRequestDTO;
import com.wireguard.external.network.ISubnet;
import com.wireguard.external.wireguard.PeerUpdateRequest;
import com.wireguard.utils.IpUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Objects;
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
                Objects.requireNonNullElse(dto.getPublicKey(), new WgKey(null)).getValue(),
                Objects.requireNonNullElse(dto.getNewPublicKey(), new WgKey(null)).getValue(),
                Objects.requireNonNullElse(dto.getPresharedKey(), new WgKey(null)).getValue(),
                allowedIps.orElse(null),
                "%s:%d".formatted(dto.getEndpoint().getHost(), dto.getEndpoint().getPort()),
                dto.getPersistentKeepalive()
        );
    }
}