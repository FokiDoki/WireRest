package com.wireguard.external.wireguard;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockingByHashAsyncExecutorTest {

    BlockingByHashAsyncExecutor<Map<String, Long>> blockingByHashAsyncExecutor = new BlockingByHashAsyncExecutor<>();
    private static final int sleepTimeMs = 10;


    static class Task {
        final long uuid = new Random().nextLong();

        @SneakyThrows
        public Map<String, Long> call() {
            Thread.sleep(sleepTimeMs);
            return Map.of("uuid", uuid, "time", System.currentTimeMillis());
        }
    }

    private List<Task> generateTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new Task());
        }
        return tasks;

    }

    private List<Future<Map<String, Long>>> generateAndRunTasks(String hash, int count) {
        List<Future<Map<String, Long>>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Task task = new Task();
            futures.add(blockingByHashAsyncExecutor.addTask(hash, task::call));
        }
        return futures;
    }

    @Test
    void testTaskIsExecutedInRightTime() {
        List<Future<Map<String, Long>>> futures = generateAndRunTasks("hash", 2);
        List<Map<String, Long>> res = waitAll(futures);
        assertTrue(Math.abs(res.get(0).get("time") - res.get(1).get("time")) > sleepTimeMs - 1);
    }

    @Test
    void testTaskIsExecutedParallel() {
        List<Future<Map<String, Long>>> futures = generateAndRunTasks("hash", 1);
        List<Future<Map<String, Long>>> futures2 = generateAndRunTasks("hash2", 1);
        List<Map<String, Long>> res = waitAll(futures);
        res.add(waitAll(futures2).get(0));
        assertTrue(Math.abs(res.get(0).get("time") - res.get(1).get("time")) < sleepTimeMs - 1);
    }

    @Test
    void isQueueFlushWhenNoTasksLeft() {
        List<Future<Map<String, Long>>> futures = generateAndRunTasks("hash", 1);
        waitAll(futures);
        Map<String, Queue<Callable<Task>>> queue = getQueue();
        assertTrue(queue.isEmpty());
    }

    @SneakyThrows
    private Map<String, Queue<Callable<Task>>> getQueue() {
        Field field = Arrays.stream(BlockingByHashAsyncExecutor.class.getDeclaredFields())
                .filter(f -> f.getName().equals("tasks")).findFirst().orElseThrow();
        field.setAccessible(true);
        return (Map<String, Queue<Callable<Task>>>) field.get(blockingByHashAsyncExecutor);
    }

    private List<Map<String, Long>> waitAll(List<Future<Map<String, Long>>> futures) {
        List<Map<String, Long>> results = new ArrayList<>();
        futures.forEach(future -> {
            try {
                results.add(future.get(sleepTimeMs * 6, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return results;
    }

}