package com.example.trades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TradeEntity {

    @Id
    private String tradeId;
    private String client;
    private String symbol;
    private int quantity;
    private double price;
    private String timestamp;

    protected TradeEntity() {}

    public TradeEntity(Trade t) {
        this.tradeId = t.tradeId();
        this.client = t.client();
        this.symbol = t.symbol();
        this.quantity = t.quantity();
        this.price = t.price();
        this.timestamp = t.timestamp();
    }

    public String getTradeId() { return tradeId; }
    public String getClient() { return client; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getTimestamp() { return timestamp; }
}