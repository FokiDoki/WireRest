package com.wireguard.api.converters;

import com.wireguard.api.dto.WgKey;
import com.wireguard.api.peer.PeerCreationRequestDTO;
import com.wireguard.external.wireguard.IpAllocationRequestOneIfNullSubnets;
import com.wireguard.external.wireguard.PeerCreationRequest;
import com.wireguard.utils.IpUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
