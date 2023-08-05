package com.wirerest.utils;

import com.wirerest.network.ISubnet;
import com.wirerest.network.Subnet;
import com.wirerest.network.SubnetV6;

import java.util.*;
import java.util.stream.Collectors;

public class IpUtils {
    private static final String IPV4_CIDR_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\/([1-3][0-2]$|[0-2][0-9]$|0?[0-9]$)";
    private static final String IPV6_REGEX = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";

    public static boolean isIpV4Cidr(String ip) {
        return ip.matches(IPV4_CIDR_REGEX);
    }

    public static boolean isIpV6Cidr(String ip) {
        List<String> ipAndCidr = List.of(ip.split("/"));
        if (ipAndCidr.size() != 2) return false;
        return isIpV6(ipAndCidr.get(0)) && isCidrV6(ipAndCidr.get(1));
    }


    private static boolean isCidrV6(String s) {
        Scanner sc = new Scanner(s);
        if (!sc.hasNextInt()) return false;
        int cidr = sc.nextInt();
        return cidr >= 0 && cidr <= 128;
    }

    public static boolean isIpV6(String ip) {
        return ip.matches(IPV6_REGEX);
    }

    public static Map<IpType, Set<ISubnet>> splitIpv4AndIpv6(Set<String> ipsStrings) {
        Map<IpType, Set<ISubnet>> ipsMap = Map.of(IpType.IPV4, new HashSet<>(), IpType.IPV6, new HashSet<>());
        if (ipsStrings == null) return ipsMap;
        ipsStrings.forEach(allowedIp -> {
            if (IpUtils.isIpV4Cidr(allowedIp)) {
                ipsMap.get(IpType.IPV4).add(Subnet.valueOf(allowedIp));
            } else {
                try {
                    ipsMap.get(IpType.IPV6).add(SubnetV6.valueOf(allowedIp));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("WgPeerParser.parseAllowedIps: Invalid IP address %s. %s".formatted(
                            allowedIp, e.getMessage()));
                }
            }

        });
        return ipsMap;
    }

    public static Set<ISubnet> stringToSubnetSet(Set<String> ipsStrings) {
        Set<ISubnet> ipsSet = new HashSet<>();
        splitIpv4AndIpv6(ipsStrings).forEach((ipType, subnets) -> ipsSet.addAll(subnets));
        return ipsSet;
    }

    public static Set<Subnet> filterV4Ips(Set<ISubnet> ips) {
        return ips.stream().filter(ip -> ip instanceof Subnet)
                .map(ip -> (Subnet) ip)
                .collect(Collectors.toSet());
    }

    public static Set<SubnetV6> filterV6Ips(Set<ISubnet> ips) {
        return ips.stream().filter(ip -> ip instanceof SubnetV6)
                .map(ip -> (SubnetV6) ip)
                .collect(Collectors.toSet());
    }

    public enum IpType {
        IPV4, IPV6
    }

}
