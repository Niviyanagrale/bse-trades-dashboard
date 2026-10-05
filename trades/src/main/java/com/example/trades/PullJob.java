package com.example.trades;

import java.util.List;

public class PullJob {

    private final String jobId;
    private volatile String status = "RUNNING";
    private volatile List<Trade> trades;

    public PullJob(String jobId) {
        this.jobId = jobId;
    }

    public void complete(List<Trade> trades) {
        this.trades = trades;
        this.status = "DONE";
    }

    public String getJobId() { return jobId; }
    public String getStatus() { return status; }
    public List<Trade> getTrades() { return trades; }
}