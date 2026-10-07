# Trades Dashboard (BSE async pull demo)

Mock BSE trade API plus a dashboard that never holds an HTTP connection
open for more than a moment, even though a full pull can take 15 minutes.

## Stack

Java 17, Spring Boot 3, Spring Web, Spring Data JPA, H2 (in-memory),
plain HTML/JS frontend. No scheduler, no polling.

## Run

```bash
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
```

Open http://localhost:8080/

Pull duration is set in `src/main/resources/application.properties`:

```properties
bse.delay-seconds=20    
```

## How to use

1. Open the dashboard. It loads instantly from the database.
2. Click **Pull now**. The page stays usable while the pull runs.
3. When the pull completes, new trades appear on every open dashboard
   automatically. No refresh needed.

## Endpoints

| Endpoint                      | Purpose                                                                           |
| ----------------------------- | --------------------------------------------------------------------------------- |
| `GET /getTrades?callbackUrl=` | Mock BSE. Replies 202 + jobId immediately, pushes trades to callbackUrl when done |
| `GET /getTrades/{jobId}`      | Mock BSE job status (RUNNING / trades)                                            |
| `POST /pull`                  | Dashboard: starts a pull                                                          |
| `POST /webhook/trades`        | Dashboard: receives the finished batch, saves it, notifies browsers               |
| `GET /api/trades`             | Trades already stored                                                             |
| `GET /api/stream`             | Server-Sent Events stream                                                         |

## Assumptions

- The spec bans schedulers, so a pull is started by the **Pull now** button.
- The real BSE call cannot be held open for 15 minutes under a 30-second
  network limit, so the mock follows a job pattern: accept, work in the
  background, call back when done.
- The mock and the dashboard live in one Spring Boot app for simplicity.
- H2 is in-memory, so data resets on restart.
- Trades are saved by tradeId, so re-pulling the same trade does not duplicate it.
