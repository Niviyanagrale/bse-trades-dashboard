package com.example.trades;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
@RestController
public class MockBseController {

    private final TradeGenerator generator;
    private final Map<String, PullJob> jobs = new ConcurrentHashMap<>();
    private final AtomicInteger pullCount = new AtomicInteger(0);
    @Value("${bse.delay-seconds}")
    private int delaySeconds;

    public MockBseController(TradeGenerator generator) {
        this.generator = generator;
    }

    // Starts a pull and answers immediately
    @GetMapping("/getTrades")
    public ResponseEntity<Map<String, String>> startPull(@RequestParam(required = false) String callbackUrl) {
        String jobId = UUID.randomUUID().toString();
        PullJob job = new PullJob(jobId);
        jobs.put(jobId, job);


        int extra = pullCount.getAndIncrement() * 200;
        // This runs in the background, so the request does not wait for it
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            job.complete(generator.generate(3000+extra));
            if(callbackUrl != null){
                new RestTemplate().postForEntity(callbackUrl,job.getTrades(),void.class);
            }
        });

        return ResponseEntity.accepted()
                .body(Map.of("jobId", jobId, "status", "RUNNING"));
    }

    // Check on a pull
    @GetMapping("/getTrades/{jobId}")
    public ResponseEntity<?> checkPull(@PathVariable String jobId) {
        PullJob job = jobs.get(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        if (job.getStatus().equals("RUNNING")) {
            return ResponseEntity.accepted()
                    .body(Map.of("jobId", jobId, "status", "RUNNING"));
        }
        return ResponseEntity.ok(job.getTrades());
    }
}