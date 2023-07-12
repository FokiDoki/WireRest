package com.wireguard.external.wireguard.converters;

/*
@Component
public class WgPeerIterableToWgPeerDTOList implements Converter<Iterable<WgPeer>, List<WgPeerDTO>> {
    @Override
    public List<WgPeerDTO> convert(Iterable<WgPeer> source) {
        return StreamSupport.stream(source.spliterator(), false)
                .map(WgPeerDTO::from)
                .collect(Collectors.toList());
    }
}
*/