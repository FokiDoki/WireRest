package com.wireguard.external.wireguard.tools;

import org.springframework.scheduling.config.Task;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RateLimitedExecutorService {
    private final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final int rateMs;
    private final Queue<Task> tasks;

    public RateLimitedExecutorService(Queue<Task> tasks, int rateMs) {
        this(tasks, rateMs, 1);
    }

    public RateLimitedExecutorService(Queue<Task> tasks, int rateMs, int poolSize) {
        this.rateMs = rateMs;
        this.tasks = tasks;
        this.executorService.setCorePoolSize(poolSize);
        start();
    }

    public void stop() {
        scheduledExecutorService.shutdown();
        executorService.shutdown();
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (executorService.getActiveCount() == executorService.getCorePoolSize()) return;
            final Task task = tasks.poll();
            if (task == null) return;
            executorService.submit(task.getRunnable());
        }, 0, rateMs, TimeUnit.MILLISECONDS);
    }
}


