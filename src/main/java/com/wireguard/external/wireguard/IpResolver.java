package com.wireguard.external.wireguard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class IpResolver {

    @Data
    @AllArgsConstructor
    private static class IpRange {
        private long least;
        private long biggest;
        public long getIpsCount(){
            return biggest - least + 1;
        }
    }

    public List<IpRange> freeRanges = new ArrayList<>();

    public IpResolver(Subnet allowedIpsSubnet){
        long firstAddress = allowedIpsSubnet.getFirstIpNumeric();
        long lastAddress = allowedIpsSubnet.getLastIpNumeric();
        freeRanges.add(new IpRange(firstAddress, lastAddress));
    }

    public Subnet takeFreeSubnet(Integer mask){
        long addressRequestCount = (long) Math.pow(2, 32-mask);
        for (int i = 0; i < freeRanges.size(); i++){
            IpRange freeRange = freeRanges.get(i);
            long freeLeast = freeRange.getLeast();
            long firstAddress = Math.ceilDiv(freeLeast-1, addressRequestCount)*addressRequestCount;
            if (firstAddress+addressRequestCount > freeRange.getIpsCount()){
                continue;
            }
            Subnet givenSubnet = new Subnet(
                    numericIpToByte(firstAddress),
                    mask);
            freeRanges.remove(i);
            freeRanges.addAll(i, insertSubnetIntoIpRange(givenSubnet, freeRange));
            return givenSubnet;
        }
        throw new NoFreeIpException();
    }

    public void takeSubnet(Subnet subnet){
        long firstAddress = subnet.getFirstIpNumeric();
        long lastAddress = subnet.getLastIpNumeric();

        for (int i = 0; i < freeRanges.size(); i++){
            IpRange freeRange = freeRanges.get(i);
            if (firstAddress >= freeRange.getLeast() && lastAddress <= freeRange.getBiggest()){
                freeRanges.remove(i);
                freeRanges.addAll(i, insertSubnetIntoIpRange(subnet, freeRange));
                return;
            }
        }
        throw new NoFreeIpException();
    }

    public void freeSubnet(Subnet subnet){
        long firstAddress = subnet.getFirstIpNumeric();
        long lastAddress = subnet.getLastIpNumeric();
        for (int i = 0; i < freeRanges.size(); i++){
            IpRange freeRange = freeRanges.get(i);
            if (firstAddress >= freeRange.getLeast() && lastAddress <= freeRange.getBiggest()){
                freeRanges.remove(i);
                freeRanges.addAll(i, insertSubnetIntoIpRange(subnet, freeRange));
                return;
            }
        }
    }

    //improve this method with binary search to return null if no greater ip found
    public int findFirstGreater(long ip){
        int left = 0;
        int right = freeRanges.size()-1;
        while (left < right){
            int mid = (left+right)/2;
            if (freeRanges.get(mid).getLeast() <= ip){
                left = mid+1;
            } else {
                right = mid;
            }
        }
        if (freeRanges.get(left).getLeast() <= ip){
            return -1;
        }
        return left;
    }



    private List<IpRange> insertSubnetIntoIpRange(Subnet subnet, IpRange ipRange){
        if (ipRange.getLeast()==subnet.getFirstIpNumeric() && ipRange.getBiggest()==subnet.getLastIpNumeric()){
            return Collections.emptyList();
        } else if (ipRange.getLeast()==subnet.getFirstIpNumeric()){
            return Collections.singletonList(new IpRange(subnet.getLastIpNumeric()+1, ipRange.getBiggest()));
        } else if (ipRange.getBiggest()==subnet.getLastIpNumeric()){
            return Collections.singletonList(new IpRange(ipRange.getLeast(), subnet.getFirstIpNumeric()-1));
        } else {
            return Arrays.asList(
                    new IpRange(ipRange.getLeast(), subnet.getFirstIpNumeric()-1),
                    new IpRange(subnet.getLastIpNumeric()+1, ipRange.getBiggest())
            );
        }
    }



    private byte[] numericIpToByte(long ip){
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--){
            result[i] = (byte) (ip % 256);
            ip /= 256;
        }
        return result;
    }

}
