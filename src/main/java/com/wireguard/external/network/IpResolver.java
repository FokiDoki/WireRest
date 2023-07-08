package com.wireguard.external.network;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;


public class IpResolver {



    private final List<IpRange> availableRanges = new ArrayList<>();
    private final IpRange totalAvailableRange;
    @Getter
    private final long totalIpsCount;
    @Getter
    private long availableIpsCount;

    public IpResolver(Subnet allowedIpsSubnet){
        long firstAddress = allowedIpsSubnet.getFirstIpNumeric();
        long lastAddress = allowedIpsSubnet.getLastIpNumeric();
        availableIpsCount = totalIpsCount = lastAddress - firstAddress + 1;
        availableRanges.add(new IpRange(firstAddress, lastAddress));
        totalAvailableRange = new IpRange(firstAddress, lastAddress);
    }

    public Subnet takeFreeSubnet(Integer mask){
        ImmutablePair<Subnet, IpRange> subnetAndRange = findFreeSubnetAndRange(mask);
        if (subnetAndRange != null){
            Subnet requestedSubnet = subnetAndRange.getLeft();
            int requestedSubnetIndex = calculatePreviousRangeIndex(findFirstGreater(requestedSubnet.getFirstIpNumeric()));
            availableRanges.remove(requestedSubnetIndex);
            availableRanges.addAll(requestedSubnetIndex, insertSubnetIntoIpRange(requestedSubnet, subnetAndRange.getRight()));
            availableIpsCount -= requestedSubnet.getIpCount();
            return requestedSubnet;
        } else {
            throw new NoFreeIpException("Cannot find free subnet with mask " + mask);
        }
    }

    public Subnet findFreeSubnet(Integer mask){
        ImmutablePair<Subnet, IpRange> result = findFreeSubnetAndRange(mask);
        return result==null ? null : result.getLeft();
    }

    private ImmutablePair<Subnet, IpRange> findFreeSubnetAndRange(Integer mask){
        long addressRequestCount = (long) Math.pow(2, 32-mask);
        for (IpRange range : availableRanges) {
            long firstAddress = Math.ceilDiv(range.getLeast(), addressRequestCount);
            long addressNeeded = firstAddress * addressRequestCount - range.getLeast() + addressRequestCount;
            if (addressNeeded > range.getIpsCount()) {
                continue;
            }
            Subnet subnet = new Subnet(
                    numericIpToByte(firstAddress * addressRequestCount),
                    mask);
            return new ImmutablePair<>(subnet, range);
        }
        return null;
    }

    public void takeIp(String ip){
        takeSubnet(Subnet.valueOf(ip+"/32"));
    }

    public void takeSubnet(Subnet subnet){
        long firstAddress = subnet.getFirstIpNumeric();
        long lastAddress = subnet.getLastIpNumeric();
        if (!isIpsInRange(firstAddress, lastAddress, totalAvailableRange)){
            throw new IllegalArgumentException("Subnet " + subnet + " is not in allowed ips range +"+ totalAvailableRange);
        }
        if (getAvailableIpsCount()==0L){
            throw new NoFreeIpException("No free ip left in "+ totalAvailableRange);
        }

        int firstGreater = findFirstGreater(firstAddress);
        int currRangeIndex = calculatePreviousRangeIndex(firstGreater);
        IpRange availableRange = availableRanges.get(currRangeIndex);
        if (isIpsInRange(firstAddress, lastAddress, availableRange)){
            availableRanges.remove(currRangeIndex);
            availableRanges.addAll(currRangeIndex, insertSubnetIntoIpRange(subnet, availableRange));
            availableIpsCount -= subnet.getIpCount();
        } else {
            throw new AlreadyUsedException("Subnet " + subnet + " is is already used");
        }
    }

    private boolean isIpsInRange(long firstAddress, long lastAddress, IpRange ipRange){
        return firstAddress >= ipRange.getLeast() && lastAddress <= ipRange.getBiggest();
    }

    private boolean isIpsNotTouchingRange(long firstAddress, long lastAddress, IpRange ipRange){
        return ipRange.getBiggest() < firstAddress || lastAddress < ipRange.getLeast();
    }

    private int calculatePreviousRangeIndex(int greaterIndex){
        if (greaterIndex==-1) {
            return availableRanges.size() - 1;
        } else {
            return Math.max(greaterIndex - 1, 0);
        }
    }


    public void freeSubnet(Subnet subnet){
        long firstAddress = subnet.getFirstIpNumeric();
        long lastAddress = subnet.getLastIpNumeric();
        if (!isIpsInRange(firstAddress, lastAddress, totalAvailableRange)){
            throw new IllegalArgumentException("Subnet " + subnet + " is not in allowed ips range +"+ totalAvailableRange);
        }
        if (getAvailableIpsCount()==0L){
            availableRanges.add(new IpRange(firstAddress, lastAddress));
            availableIpsCount += subnet.getIpCount();
            return;
        }

        int greaterIndex = findFirstGreater(firstAddress);
        int leastIndex = calculatePreviousRangeIndex(greaterIndex);
        IpRange greater = availableRanges.get(greaterIndex);
        IpRange least = availableRanges.get(leastIndex);
        if (isIpsNotTouchingRange(firstAddress, lastAddress, least)){
            if (greater.getLeast()-1 == lastAddress && least.getBiggest()+1 == firstAddress){
                availableRanges.removeAll(Arrays.asList(greater, least));
                availableRanges.add(leastIndex, new IpRange(least.getLeast(), greater.getBiggest()));
            } else if (greater.getLeast()-1 == lastAddress){
                availableRanges.remove(greater);
                availableRanges.add(leastIndex, new IpRange(firstAddress, greater.getBiggest()));
            } else if (least.getBiggest()+1 == firstAddress){
                availableRanges.remove(least);
                availableRanges.add(leastIndex, new IpRange(least.getLeast(), lastAddress));
            } else {
                availableRanges.add(leastIndex, new IpRange(firstAddress, lastAddress));
            }
            availableIpsCount += subnet.getIpCount();
        } else {
            throw new UncheckedIOException(new IOException("This subnet is not used"));
        }
    }

    private int findFirstGreater(long ip){
        int left = 0;
        int right = availableRanges.size()-1;
        while (left < right){
            int mid = (left+right)/2;
            if (availableRanges.get(mid).getLeast() <= ip){
                left = mid+1;
            } else {
                right = mid;
            }
        }
        if (availableRanges.get(left).getLeast() <= ip){
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

    public long getUsedIpsCount(){
        return totalIpsCount - availableIpsCount;
    }

    public List<IpRange> getAvailableRanges(){
        return Collections.unmodifiableList(availableRanges);
    }


    private byte[] numericIpToByte(long ip){
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--){
            result[i] = (byte) (ip % 256);
            ip /= 256;
        }
        return result;
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IpRange {
        private long least;
        private long biggest;
        public long getIpsCount(){
            return biggest - least + 1;
        }
    }

}
