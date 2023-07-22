package com.wireguard.external.wireguard;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class race {
    private static final ReentrantLock ok = new ReentrantLock();
    @SneakyThrows
    static void task(){
        ok.lock();
        Thread.sleep(1000);
        System.out.println("I'm ok");
        ok.unlock();
    }

    @SneakyThrows
    static void task2(){
        ok.lock();
        Thread.sleep(1000);
        System.out.println("I'm ok 2");
        ok.unlock();
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            service.submit(race::task);
            service.submit(race::task2);
        }
    }
}
