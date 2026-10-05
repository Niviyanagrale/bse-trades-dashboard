package com.example.trades;

public record Trade(String tradeId, String client, String symbol,
                    int quantity, double price, String timestamp) {}