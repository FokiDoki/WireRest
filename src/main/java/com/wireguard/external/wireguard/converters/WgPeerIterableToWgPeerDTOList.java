package com.wireguard.external.wireguard.converters;

import com.wireguard.external.wireguard.WgPeer;
import com.wireguard.external.wireguard.dto.WgPeerDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class WgPeerIterableToWgPeerDTOList implements Converter<Iterable<WgPeer>, List<WgPeerDTO>> {
    @Override
    public List<WgPeerDTO> convert(Iterable<WgPeer> source) {
        return StreamSupport.stream(source.spliterator(), false).map(WgPeerDTO::from).collect(Collectors.toList());
    }
}
