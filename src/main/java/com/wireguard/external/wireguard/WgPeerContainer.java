package com.wireguard.external.wireguard;

import com.wireguard.external.wireguard.dto.WgPeerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class WgPeerContainer implements PagingAndSortingRepository<WgPeer, String> {
    private List<WgPeer> peers;

    public WgPeerContainer(){
        peers = new ArrayList<>();
    }
    public void addPeer(WgPeer peer){
        peers.add(peer);
    }

    public void addPeers(Collection<WgPeer> peersForAdd){
        peers.addAll(peersForAdd);
    }

    public void removePeer(WgPeer peer){
        peers.remove(peer);
    }

    public void removePeer(int index){
        peers.remove(index);
    }

    public void removePeerByPublicKey(String publicKey){
        Optional<WgPeer> peer = getByPublicKey(publicKey);
        peer.ifPresent(this::removePeer);
    }

    public void clearPeers(){
        peers.clear();
    }

    public WgPeer getPeer(int index){
        return peers.get(index);
    }

    public int size(){
        return peers.size();
    }

    public Optional<WgPeer> getByPublicKey(String publicKey){
        for(WgPeer peer : peers){
            if(peer.getPublicKey().equals(publicKey)){
                return Optional.of(peer);
            }
        }
        return Optional.empty();
    }

    public Optional<WgPeer> getByPresharedKey(String presharedKey){
        for(WgPeer peer : peers){
            if(peer.getPresharedKey().equals(presharedKey)){
                return Optional.of(peer);
            }
        }
        return Optional.empty();
    }

    public Optional<WgPeerDTO> getDTOByPublicKey(String publicKey){
        Optional<WgPeer> peer = getByPublicKey(publicKey);
        return peer.map(WgPeerDTO::from);
    }

    public Optional<WgPeerDTO> getDTOByPresharedKey(String presharedKey){
        Optional<WgPeer> peer = getByPresharedKey(presharedKey);
        return peer.map(WgPeerDTO::from);
    }

    public Set<String> getIpv4Addresses(){
        return peers.stream()
                .map(peer -> peer.getAllowedIps().getIPv4IPs())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Set<WgPeer> toSet(){
        return new HashSet<>(peers);
    }

    public Set<WgPeerDTO> toDTOSet(){
        return peers.stream()
                .map(WgPeerDTO::from)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @Override
    public Iterable<WgPeer> findAll(Sort sort) {
        List<WgPeer> sortedPeers = (List<WgPeer>) sortList(sort.iterator(), peers);
        return sortedPeers;
    }

    private List<?> sortList(Iterator<Sort.Order> orders, List<?> list) throws NoSuchFieldException {
        if (!orders.hasNext()) {
            return list;
        }
        Sort.Order order = orders.next();
        Field field = list.get(0).getClass().getDeclaredField(order.getProperty());
        field.setAccessible(true);

        Comparator<Object> comparator = (o1, o2) -> {
            try {
                return field.get(o1).toString().compareTo(field.get(o2).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't compare. ", e);
            }
        };
        if (order.isAscending()) {
            comparator = comparator.reversed();
        }
        list.sort(comparator);
        return sortList(orders, list);
    }

    @Override
    public Page<WgPeer> findAll(Pageable pageable) {
        return null;
    }
}
