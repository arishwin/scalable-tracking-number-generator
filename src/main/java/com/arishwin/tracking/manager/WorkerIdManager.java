package com.arishwin.tracking.manager;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerIdManager {

    private final StringRedisTemplate redis;
    private final String instanceId;
    private final static int MAX_WORKERS = 1024;
    private final static long LEASE_TTL_SECONDS = 60;

    private volatile int workerId = -1;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public WorkerIdManager(StringRedisTemplate redis) {
        this.redis = redis;
        this.instanceId = UUID.randomUUID().toString();
    }

    public int getWorkerId() {
        if (workerId == -1) {
            throw new IllegalStateException("Worker ID not initialized");
        }
        return workerId;
    }

    @PostConstruct
    public void init() {
        // loop through worker IDs and try to acquire a lease
        for (int i = 0; i < MAX_WORKERS; i++) {
            String key = "worker:" + i;
            Boolean success = redis.opsForValue().setIfAbsent(key, instanceId, LEASE_TTL_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(success)) {
                workerId = i;
                final int id = i;
                scheduler.scheduleAtFixedRate(() -> renewLease(id), 30, 30, TimeUnit.SECONDS);
                return;
            }
        }
        throw new IllegalStateException("No available worker IDs in Redis");
    }

    private void renewLease(int id) {
        String key = "worker:" + id;
        String currentHolder = redis.opsForValue().get(key);
        if (Objects.equals(currentHolder, instanceId)) {
            redis.expire(key, LEASE_TTL_SECONDS, TimeUnit.SECONDS);
        }
    }

    @PreDestroy
    public void releaseWorkerId() {
        if (workerId >= 0) {
            String key = "worker:" + workerId;
            String currentHolder = redis.opsForValue().get(key);
            if (Objects.equals(currentHolder, instanceId)) {
                redis.delete(key);
                System.out.println("Released workerId = " + workerId);
            }
        }
        scheduler.shutdownNow();
    }
}