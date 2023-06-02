package com.wireguard.parser;

import com.wireguard.external.wireguard.WgInterface;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class WgInterfaceParser {

    public static WgInterface parse(String wgShowStringWithInterfaceData, String splitter){
        String[] splitInterfaceData = wgShowStringWithInterfaceData.split(splitter);
        return parse(List.of(splitInterfaceData));
    }

    public static WgInterface parse(List<String> wgShowArrayWithInterfaceData){
        Assert.isTrue(wgShowArrayWithInterfaceData.size() == 4,
                "WgInterfaceParser.parse: invalid number of arguments in %s, 4 expected".formatted(
                        wgShowArrayWithInterfaceData.toString()
                ));
        List<String> wgShowInterfaceData =
                Utils.trimAll(wgShowArrayWithInterfaceData);
        return new WgInterface(
                wgShowInterfaceData.get(0),
                wgShowInterfaceData.get(1),
                Integer.parseInt(wgShowInterfaceData.get(2)),
                parseFwmark(wgShowInterfaceData.get(3))
                );
    }


    private static int parseFwmark(String fwmark){
        if (fwmark.equals("off")) return 0;
        return Integer.parseInt(fwmark);
    }
}
