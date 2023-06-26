package com.wireguard.external.network;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;

import java.net.Inet4Address;
import java.util.List;

@EqualsAndHashCode
public class Subnet implements Comparable<Subnet> {
    private final byte[] ip;
    private final byte[] mask;
    @Getter private final int numericMask;
    private static final String IP_VALIDATE_REGEX = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)(\\.(?!$)|$)){4}$";


    public static Subnet valueOf(String subnet){
        Assert.isTrue(subnet.contains("/"), subnet+ " is not a valid subnet");
        String ip = subnet.split("/")[0];
        int mask = Integer.parseInt(subnet.split("/")[1]);
        return new Subnet(parseIp(ip), mask);
    }

    public static Subnet valueOf(Inet4Address inet4Address, int mask){
        return new Subnet(inet4Address.getAddress(), mask);
    }

    public Subnet(byte[] ip, int mask){
        this.ip = ip;
        this.mask = parseMask(mask);
        this.numericMask = mask;
    }

    public long getIpCount(){
        return (long) Math.pow(2, 32 - numericMask);
    }

    public byte[] getFirstIpBytes(){
        byte[] firstIp = new byte[4];
        for(int i = 0; i < 4; i++){
            firstIp[i] = (byte) (ip[i] & mask[i]);
        }
        return firstIp;
    }

    @Override
    public String toString() {
        return getIpString() + "/" + numericMask;
    }

    public byte[] getLastIpBytes(){
        byte[] lastIp = new byte[4];
        for (int i = 0; i < 4; i++){
            lastIp[i] = (byte) (ip[i] | ~mask[i]);
        }
        return lastIp;
    }


    public long getLastIpNumeric(){
        return byteToNumericIp(getLastIpBytes());
    }

    public long getFirstIpNumeric(){
        return byteToNumericIp(getFirstIpBytes());
    }

    private List<Integer> byteIpToIntList(byte[] ip){
        return List.of(
                byteToUnsignedInt(ip[0]),
                byteToUnsignedInt(ip[1]),
                byteToUnsignedInt(ip[2]),
                byteToUnsignedInt(ip[3])
        );
    }

    public List<Integer> getIp(){
        return byteIpToIntList(ip);
    }

    private String bytesIpToString(byte[] ip){
        return "%d.%d.%d.%d".formatted(
                byteToUnsignedInt(ip[0]),
                byteToUnsignedInt(ip[1]),
                byteToUnsignedInt(ip[2]),
                byteToUnsignedInt(ip[3])
        );
    }

    public String getIpString(){
        return bytesIpToString(ip);
    }

    public String getFirstIpString(){
        return bytesIpToString(getFirstIpBytes());
    }

    public String getLastIpString(){
        return bytesIpToString(getLastIpBytes());
    }

    public List<Integer> getFirstIp(){
        return byteIpToIntList(getFirstIpBytes());
    }

    public List<Integer> getLastIp(){
        return byteIpToIntList(getLastIpBytes());
    }

    private long byteToNumericIp(byte[] ip){
        long count = 0;
        for (int i = 0; i < 4; i++){
            count += byteToUnsignedInt(ip[i]) * Math.pow(256, 3-i);
        }
        return count;
    }

    private int byteToUnsignedInt(Byte b){
        return b & 0xFF;
    }


    private static byte[] parseIp(String ipStr){
        if (!ipStr.matches(IP_VALIDATE_REGEX)) {
            throw new IllegalArgumentException(
                    "Subnet.parseIp: ip value %s is not a valid ip".formatted(ipStr)
            );
        }
        String[] ipParts = ipStr.split("\\.");
        byte[] ip = new byte[4];
        for(int i = 0; i < 4; i++){
            ip[i] = (byte) (Integer.parseInt(ipParts[i]));
        }
        return ip;
    }

    private static byte[] parseMask(int mask){
        if(mask < 0 || mask > 32){
                throw new IllegalArgumentException(
                    "Subnet.parseMask: mask value %d out of range".formatted(mask)
            );
        }
        byte[] maskBytes = new byte[4];
        for(int i = 0; i < 4; i++){
            if(mask >= 8){
                maskBytes[i] = (byte) 255;
                mask -= 8;
            } else if(mask > 0){
                maskBytes[i] = (byte) (255 - (Math.pow(2, 8 - mask) - 1));
                mask = 0;
            } else {
                maskBytes[i] = (byte) 0;
            }
        }
        return maskBytes;
    }

    @Override
    public int compareTo(Subnet o) {
        return Long.compare(getFirstIpNumeric(), o.getFirstIpNumeric());
    }
}
