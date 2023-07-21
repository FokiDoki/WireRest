package com.wireguard.external.wireguard.peer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PeerCreationRules {


    private final int defaultMask;
    private final int defaultIpsToGenerate = 1;

    @Autowired
    public PeerCreationRules(@Value("${wg.interface.default.mask}") int defaultMask) {
        this.defaultMask = defaultMask;
    }

}
