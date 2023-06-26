package com.wireguard.external.wireguard.converters;

import com.wireguard.external.wireguard.WgPeerContainer;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WgPeerContainerToWgPeerDTOSet implements Converter<WgPeerContainer, Set<WgPeerDTO>> {
    @Override
    public Set<WgPeerDTO> convert(WgPeerContainer source) {
        return source.stream()
                .map(WgPeerDTO::from)
                .collect(Collectors.toSet());
    }
}
