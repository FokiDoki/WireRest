package com.wireguard.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    private static final String IPV4_CIDR_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\/([1-3][0-2]$|[0-2][0-9]$|0?[0-9]$)";
    private static final String IPV6_REGEX = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";

    public static ArrayList<String> trimAll(List<String> args){
        ArrayList<String> trimmedArgs = new ArrayList<>();
        for (String arg : args) {
            trimmedArgs.add(arg.trim());
        }
        return trimmedArgs;
    }

    public static boolean isIpV4Cidr(String ip){
        return ip.matches(IPV4_CIDR_REGEX);
    }

    public static boolean isIpV6Cidr(String ip){
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

    public static boolean isIpV6(String ip){
        return ip.matches(IPV6_REGEX);
    }

}
