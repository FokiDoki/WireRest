package com.wireguard.parser;

import com.wireguard.DTO.WgInterface;

import java.util.List;

public class WgInterfaceParser {
    public static WgInterface parse(List<String> args){
        return new WgInterface(args.get(0),
                args.get(1),
                Integer.parseInt(args.get(2)),
                Integer.parseInt(args.get(3)));
    }
}
