package com.wirerest.wireguard.tools;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class BlockingByHashAsyncExecutor<T> {
    private final Map<String, Queue<Callable<T>>> tasks = Collections.synchronizedMap(new LinkedHashMap<>());

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Future<T> addTask(String hash, Callable<T> task) {
        synchronized (tasks) {
            Queue<Callable<T>> queue = tasks.computeIfAbsent(hash, k -> new LinkedBlockingQueue<>());
            queue.add(task);
        }

        return executor.submit(() -> {
            Queue<Callable<T>> queue = tasks.get(hash);
            T taskResult;
            synchronized (queue) {
                Callable<T> tCallable = queue.poll();
                assert tCallable != null;
                taskResult = tCallable.call();
                synchronized (tasks) {
                    if (queue.isEmpty()) {
                        tasks.remove(hash);
                    }
                }
            }

            return taskResult;

        });
    }


}
