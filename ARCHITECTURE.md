# Architecture

```mermaid
sequenceDiagram
    participant B as Browser
    participant D as Dashboard API
    participant M as Mock BSE
    participant DB as H2 database

    B->>D: GET /api/trades (instant, from DB)
    B->>D: GET /api/stream (SSE, stays open)
    B->>D: POST /pull
    D->>M: GET /getTrades?callbackUrl=...
    M-->>D: 202 Accepted + jobId (instant)
    Note over M: slow work runs in background (up to 15 min)
    M->>D: POST /webhook/trades (full batch)
    D->>DB: save trades
    D-->>B: SSE event "trades-updated"
    B->>D: GET /api/trades (refresh table)
```

## Why this design

- **No connection is ever held for 15 minutes.** The pull request is
  acknowledged immediately and the result is delivered by callback, so the
  30-second network limit never applies to the slow part.
- **The database is the source of truth.** The dashboard reads stored
  trades, so it opens instantly even while a pull is running.
- **SSE instead of polling.** The server pushes an event when new data
  lands, so there is no polling loop and no scheduler.
- **Known trade-off:** the SSE connection is long-lived, so a network with a
  30-second limit can drop it. The browser's EventSource reconnects
  automatically, and the page reloads from the database on every reconnect
  (`source.onopen`), so no update is lost.
