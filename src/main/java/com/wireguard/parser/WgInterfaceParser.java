package com.wireguard.parser;

import com.wireguard.external.wireguard.dto.WgInterface;
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
                parseAndValidatePrivateKey(wgShowInterfaceData.get(0)),
                parseAndValidatePublicKey(wgShowInterfaceData.get(1)),
                parseAndValidateListenPort(wgShowInterfaceData.get(2)),
                parseAndValidateFwmark(wgShowInterfaceData.get(3))
                );
    }


    private static String parseAndValidatePrivateKey(String privateKey){
        return Validator.validateKey(privateKey);
    }

    private static String parseAndValidatePublicKey(String publicKey){
        return Validator.validateKey(publicKey);
    }

    private static int parseAndValidateListenPort(String listenPort){
        return Validator.validatePort(listenPort);
    }

    private static int parseAndValidateFwmark(String fwmark){
        if (fwmark.equals("off")) return 0;
        return Integer.parseInt(fwmark);
    }
}
