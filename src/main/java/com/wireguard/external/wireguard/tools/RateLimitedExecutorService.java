package com.wireguard.external.wireguard.tools;

import org.springframework.scheduling.config.Task;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class RateLimitedExecutorService {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final int rateMs;
    private final Queue<Task> tasks;
    public RateLimitedExecutorService(Queue<Task> tasks, int rateMs){
        this.rateMs = rateMs;
        this.tasks = tasks;
        start();
    }

    public void stop(){
        scheduledExecutorService.shutdown();
        executorService.shutdown();
    }

    public void start(){
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final Task task = tasks.poll();
            if (task == null) return;
            executorService.submit(task.getRunnable());
        }, 0, rateMs, TimeUnit.MILLISECONDS);
    }
}


