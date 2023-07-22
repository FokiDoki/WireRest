package com.wireguard.external.wireguard;

import com.wireguard.external.network.ISubnet;
import com.wireguard.external.network.IV4SubnetSolver;
import com.wireguard.external.network.Subnet;
import com.wireguard.external.wireguard.peer.PeerCreationRules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class SubnetServiceTest {

    PeerCreationRules peerCreationRules = Mockito.mock(PeerCreationRules.class);
    IV4SubnetSolver subnetSolver = Mockito.mock(IV4SubnetSolver.class);
    private final SubnetService subnetService = new SubnetService(peerCreationRules, subnetSolver);

    public SubnetServiceTest(){
        Mockito.when(subnetSolver.obtainFree(Mockito.anyInt())).then((Answer<Subnet>) invocation -> getRandomSubnetMock());
        Mockito.when(peerCreationRules.getDefaultMask()).thenReturn(32);
    }

    private ISubnet getRandomISubnetMock(){
        return Mockito.mock(ISubnet.class);

    }

    private Set<ISubnet> generateRandomISubnets(int count){
        Set<ISubnet> subnets = new HashSet<>();
        for (int i = 0; i < count; i++) {
            subnets.add(getRandomISubnetMock());
        }
        return subnets;
    }

    private Set<Subnet> generateRandomSubnets(int count){
        Set<Subnet> subnets = new HashSet<>();
        for (int i = 0; i < count; i++) {
            subnets.add(getRandomSubnetMock());
        }
        return subnets;
    }
    private Subnet getRandomSubnetMock(){
        return Mockito.mock(Subnet.class);
    }

    @BeforeAll
    static void setUp(){

    }
    void generateV4(int mask, int count) {
        Set<Subnet> generatedSubnets = subnetService.generateV4(count, mask);
        Mockito.verify(subnetSolver, Mockito.times(count)).obtainFree(mask);
        Assertions.assertNotNull(generatedSubnets);
        Assertions.assertEquals(generatedSubnets.size(), count);
    }
    @Test
    void generateV4Test(){
        generateV4(30, 10);
    }

    @Test
    void generateV4Count0() {
        generateV4(30, 0);
    }

    @Test
    void generateV4OneArgument() {
        subnetService.generateV4(10);
        Mockito.verify(peerCreationRules, Mockito.times(1)).getDefaultMask();
    }
    @Test
    void generateV6() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> subnetService.generateV6(32,1));
    }

    void release(Set<? extends ISubnet> subnets) {
        subnetService.release(subnets);
        Mockito.verify(subnetSolver, Mockito.times(subnets.size())).release(Mockito.any(Subnet.class));
    }

    @Test
    void releaseTest(){
        release(generateRandomSubnets(10));
    }

    @Test
    void releaseTestCount0(){
        release(generateRandomSubnets(0));
    }

    @Test
    void releaseTestNull(){
        Assertions.assertThrows(NullPointerException.class, () -> release(null));
    }

    void obtain(Set<? extends ISubnet> subnets) {
        subnetService.obtain(subnets);
        Mockito.verify(subnetSolver, Mockito.times(subnets.size())).obtain(Mockito.any(Subnet.class));
    }

    @Test
    void obtainTest(){
        obtain(generateRandomSubnets(10));
    }

    @Test
    void obtainTestCount0(){
        obtain(generateRandomSubnets(0));
    }

    @Test
    void obtainTestNull(){
        Assertions.assertThrows(NullPointerException.class, () -> obtain(null));
    }

    @Test
    void obtainException(){
        Mockito.doNothing()
                .doThrow(new RuntimeException())
                .when(subnetSolver).obtain(Mockito.any(Subnet.class));
        Assertions.assertThrows(RuntimeException.class, () -> obtain(generateRandomSubnets(2)));
        Mockito.verify(subnetSolver, Mockito.times(1)).release(Mockito.any(Subnet.class));

    }

    void applyState(Set<? extends ISubnet> before, Set<? extends ISubnet> after) {
        subnetService.applyState(before, after);
        Set<? extends ISubnet> subnetsToRelease = before.stream().filter(i -> !after.contains(i)).collect(Collectors.toSet());
        Set<? extends ISubnet> subnetsToObtain = after.stream().filter(i -> !before.contains(i)).collect(Collectors.toSet());
        Mockito.verify(subnetSolver, Mockito.times(subnetsToRelease.size())).release(Mockito.any(Subnet.class));
        Mockito.verify(subnetSolver, Mockito.times(subnetsToObtain.size())).obtain(Mockito.any(Subnet.class));

    }

    @Test
    void testApplyState(){
        applyState(generateRandomSubnets(10), generateRandomSubnets(10));
    }

    @Test
    void testApplyStateCount0(){
        applyState(generateRandomSubnets(0), generateRandomSubnets(0));
    }

    @Test
    void testApplyStateNull(){
        Assertions.assertThrows(NullPointerException.class, () -> applyState(null, null));
    }

    @Test
    void testApplyStateNullBefore(){
        Assertions.assertThrows(NullPointerException.class, () -> applyState(null, generateRandomSubnets(10)));
    }

    @Test
    void testApplyStateNullAfter(){
        Assertions.assertThrows(NullPointerException.class, () -> applyState(generateRandomSubnets(10), null));
    }

    @Test
    void testApplyStateBeforeIncludeAfter(){
        Set<Subnet> before = generateRandomSubnets(10);
        Set<Subnet> after = new HashSet<>(before);
        applyState(before, after);
    }

    @Test
    void testApplyStateAfterIncludeBefore(){
        Set<Subnet> after = generateRandomSubnets(10);
        Set<Subnet> before = new HashSet<>(after);
        applyState(before, after);
    }
}