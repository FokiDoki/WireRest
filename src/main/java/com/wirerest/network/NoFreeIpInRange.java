package com.wirerest.network;

public class NoFreeIpInRange extends NoFreeIpException{
    public NoFreeIpInRange(IV4SubnetSolver.IpRange range) {
        super("The range %s has no free ip that can be assigned".formatted(range.toString()));
    }

    public NoFreeIpInRange(String startIp, String endIp) {
        super("The range %s-%s has no free ip that can be assigned".formatted(startIp, endIp));
    }
}
