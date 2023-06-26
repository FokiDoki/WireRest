package com.wireguard.external.wireguard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class WgPeerContainer extends TreeSet<WgPeer> implements IWgPeerContainer<WgPeer> {

    public WgPeerContainer(Set<WgPeer> peers){
        super(peers);
    }

    public void removePeerByPublicKey(String publicKey){
        Optional<WgPeer> peer = getByPublicKey(publicKey);
        peer.ifPresent(super::remove);
    }

    public Optional<WgPeer> getByPublicKey(String publicKey){
        return super.stream().filter(
                p -> p.getPublicKey().equals(publicKey)
            ).findFirst();
    }

    public Optional<WgPeer> getByPresharedKey(String presharedKey){
        return super.stream()
                .filter(p -> p.getPresharedKey() != null)
                .filter(p -> p.getPresharedKey().equals(presharedKey))
                .findFirst();
    }

    public Set<String> getIpv4Addresses(){
        return super.stream()
                .map(peer -> peer.getAllowedIps().getIPv4IPs())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @Override
    public Iterable<WgPeer> findAll(Sort sort)  {
        return sortList(sort.iterator(), new ArrayList<>(this));
    }

    @SuppressWarnings("unchecked")
    private List<WgPeer> sortList(Iterator<Sort.Order> orders, List<WgPeer> list) throws NoSuchFieldException {
        if (!orders.hasNext()) {
            return list;
        }
        Sort.Order order = orders.next();
        Field field = WgPeer.class.getDeclaredField(order.getProperty());
        field.setAccessible(true);

        Comparator<Object> comparator = (o1, o2) -> {
            try {
                if (field.get(o1) == null && field.get(o2) == null) {
                    return 0;
                } else if (field.get(o1) == null) {
                    return -1;
                } else if (field.get(o2) == null) {
                    return 1;
                }
                if (field.get(o1) instanceof Comparable){
                    return ((Comparable<Object>) field.get(o1)).compareTo(field.get(o2));
                }
                return field.get(o1).toString().compareTo(field.get(o2).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't compare. ", e);
            }
        };
        if (order.isDescending()) {
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
