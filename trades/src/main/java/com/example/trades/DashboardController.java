package com.example.trades;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
public class DashboardController {

    private final TradeRepository repository;
    private final TradeEventService events;

    public DashboardController(TradeRepository repository, TradeEventService events) {
        this.repository = repository;
        this.events = events;
    }

    @PostMapping("/pull")
    public ResponseEntity<Map<String, String>> startPull() {
        String url = "http://localhost:8080/getTrades?callbackUrl=http://localhost:8080/webhook/trades";
        new RestTemplate().getForEntity(url, String.class);
        return ResponseEntity.accepted().body(Map.of("status", "Pull started"));
    }

    @PostMapping("/webhook/trades")
    public void receiveTrades(@RequestBody List<Trade> trades) {
        repository.saveAll(trades.stream().map(TradeEntity::new).toList());
        events.broadcast("trades-updated", String.valueOf(trades.size()));
    }

    @GetMapping("/api/trades")
    public List<TradeEntity> allTrades() {
        return repository.findAll();
    }

    // The open dashboard connects here and waits for messages
    @GetMapping(value = "/api/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return events.subscribe();
    }
}