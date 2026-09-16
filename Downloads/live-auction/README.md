# Live Auction

A real-time bidding platform. React + TypeScript frontend, Spring Boot backend,
STOMP-over-WebSocket for live price updates, optimistic locking for safe
concurrent bids.

## Stack

- **Backend**: Java 21, Spring Boot 3.3, Spring WebSocket (STOMP/SockJS), Spring Data JPA
- **Frontend**: React 18, TypeScript, Vite, Tailwind CSS, @stomp/stompjs
- **DB**: H2 in-memory for local dev (swap to Postgres for deployment — see `application.properties`)

## Running locally

**Backend** (needs Java 21 + Maven):
```
cd backend
mvn spring-boot:run
```
Runs on `http://localhost:8080`. Seeds two demo auctions on startup.
H2 console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:auctiondb`).

**Frontend** (needs Node 18+):
```
cd frontend
npm install
npm run dev
```
Runs on `http://localhost:5173`.

Open two browser windows side by side and bid from both — you'll see updates
push to both instantly.

## How it works

1. Frontend loads the auction list via REST (`GET /api/auctions`).
2. On selecting an auction, it opens a STOMP connection over SockJS to `/ws`
   and subscribes to `/topic/auctions/{id}`.
3. Placing a bid sends a STOMP message to `/app/auctions/{id}/bid`.
4. The backend validates the bid (higher than current price, auction still
   open), persists it, and broadcasts the result to every subscriber of that
   topic — including the bidder who just placed it.

## The concurrency problem this solves

Two bidders can submit near-simultaneously. Without protection, a classic
lost-update race lets a lower bid silently overwrite a higher one (see the
comment in `AuctionService.placeBid` for the full walkthrough). This project
uses **optimistic locking** (`@Version` on the `Auction` entity): every update
checks the row hasn't changed since it was read, and a bounded retry loop
handles the rare collision. This is chosen over pessimistic row-locking
because bidding is low-contention per row and optimistic locking scales
better under load — a good thing to be able to explain in an interview.

## Where to take it next

- JWT auth (endpoints are open right now — good next step given your OnSite-io
  Auth0/JWT experience)
- Redis pub/sub if you scale the WebSocket broker beyond one instance
- Auction countdown timer + auto-close job
- Deploy: backend to Railway/Render/Fly.io, frontend to Vercel
