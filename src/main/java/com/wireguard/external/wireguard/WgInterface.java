package com.wireguard.external.wireguard;


public record WgInterface(String name, String ip, Subnet subnet){
}
