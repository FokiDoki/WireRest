package com.wireguard.external.network;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.wireguard.utils.AsyncUtils.await;

public class QueuedSubnetSolver implements IV4SubnetSolver {

    private final IV4SubnetSolver subnetSolver;


    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public QueuedSubnetSolver(IV4SubnetSolver subnetSolver){
        this.subnetSolver = subnetSolver;
    }

    @Override
    public Subnet obtainFree(int mask) {
        Future<Subnet> subnetFuture = executor.submit(() -> subnetSolver.obtainFree(mask));
        try {
            return subnetFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void obtain(Subnet subnet) throws AlreadyUsedException{
        await(executor.submit(() -> subnetSolver.obtain(subnet)));
    }

    @Override
    public void obtainIp(String ip) {
        await(executor.submit(() -> subnetSolver.obtainIp(ip)));
    }

    @Override
    public void release(Subnet subnet) {
        await(executor.submit(() -> subnetSolver.release(subnet)));
    }

    @SneakyThrows
    @Override
    public boolean isUsed(Subnet subnet) {
        Future<Boolean> isUsedFuture = executor.submit(() -> subnetSolver.isUsed(subnet));
        await(isUsedFuture);
        return isUsedFuture.get();
    }



    @Override
    public long getAvailableIpsCount() {
        return subnetSolver.getAvailableIpsCount();
    }

    @Override
    public long getTotalIpsCount() {
        return subnetSolver.getTotalIpsCount();
    }

    @Override
    public long getUsedIpsCount() {
        return subnetSolver.getUsedIpsCount();
    }
}
