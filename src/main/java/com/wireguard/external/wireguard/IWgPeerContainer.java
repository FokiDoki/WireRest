package com.wireguard.external.wireguard;

import org.springframework.data.repository.PagingAndSortingRepository;

interface IWgPeerContainer<T extends WgPeer> extends PagingAndSortingRepository<T, String>{
}
