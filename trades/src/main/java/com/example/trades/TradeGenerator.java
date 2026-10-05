package com.example.trades;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class TradeGenerator {

    private static final String[] CLIENTS = {"Aarav", "Meera", "Rohan", "Isha", "Kabir", "Anaya"};
    private static final String[] SYMBOLS = {"RELIANCE", "TCS", "INFY", "HDFCBANK", "SBIN", "ITC", "LT"};

    public List<Trade> generate(int count) {
        Random random = new Random(42); // same seed = same data every time
        LocalDateTime start = LocalDateTime.of(2026, 10, 5, 9, 15);
        List<Trade> trades = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            trades.add(new Trade(
                    "T" + String.format("%05d", i),
                    CLIENTS[random.nextInt(CLIENTS.length)],
                    SYMBOLS[random.nextInt(SYMBOLS.length)],
                    1 + random.nextInt(500),
                    Math.round((100 + random.nextDouble() * 2900) * 100.0) / 100.0,
                    start.plusSeconds(i * 5L).toString()
            ));
        }
        return trades;
    }
}