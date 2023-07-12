package com.wireguard.external.wireguard.peer;

import org.springframework.data.repository.PagingAndSortingRepository;

interface IWgPeerContainer<T extends WgPeer> extends PagingAndSortingRepository<T, String>{
}
