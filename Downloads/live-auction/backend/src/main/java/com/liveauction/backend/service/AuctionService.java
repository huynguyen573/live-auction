package com.liveauction.backend.service;

import com.liveauction.backend.dto.BidRequest;
import com.liveauction.backend.dto.BidUpdateMessage;
import com.liveauction.backend.dto.CreateAuctionRequest;
import com.liveauction.backend.model.Auction;
import com.liveauction.backend.model.Bid;
import com.liveauction.backend.repository.AuctionRepository;
import com.liveauction.backend.repository.BidRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Objects;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    // Each retry needs its own transaction. @Transactional on the outer loop
    // doesn't work: once OptimisticLockingFailureException is thrown the
    // transaction is marked rollback-only, so the next iteration would just
    // re-read stale first-level cache data and fail again. TransactionTemplate
    // gives each attempt a fresh transaction instead.
    private final TransactionTemplate txTemplate;

    public AuctionService(AuctionRepository auctionRepository, BidRepository bidRepository,
                          PlatformTransactionManager txManager) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.txTemplate = new TransactionTemplate(txManager);
    }

    public Auction createAuction(CreateAuctionRequest req) {
        Auction auction = new Auction(req.title(), req.description(), req.startingPrice(), req.endsAt());
        return auctionRepository.save(auction);
    }

    /**
     * Places a bid safely under concurrent access.
     *
     * Why this matters: two users can click "bid" within milliseconds of each other.
     * Without protection, a classic lost-update race can happen:
     *   1. User A reads currentPrice = 100
     *   2. User B reads currentPrice = 100
     *   3. User A writes currentPrice = 110  (their bid of 110)
     *   4. User B writes currentPrice = 105  (their bid of 105, based on stale read)
     *      -> B's lower bid overwrites A's higher one. Silent data corruption.
     *
     * @Version on Auction makes every UPDATE include "WHERE version = ?".
     * If the row changed since we read it, Hibernate throws
     * OptimisticLockingFailureException instead of applying a stale write.
     * We catch that and retry up to maxRetries times, each in a fresh transaction.
     */
    public BidUpdateMessage placeBid(Long auctionId, BidRequest request) {
        final int maxRetries = 3;
        OptimisticLockingFailureException lastFailure = null;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return Objects.requireNonNull(
                        txTemplate.execute(status -> attemptBid(auctionId, request)));
            } catch (OptimisticLockingFailureException e) {
                lastFailure = e;
            }
        }
        throw lastFailure;
    }

    private BidUpdateMessage attemptBid(Long auctionId, BidRequest request) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

        if (Instant.now().isAfter(auction.getEndsAt())) {
            return new BidUpdateMessage(auctionId, auction.getCurrentPrice(), request.bidderName(),
                    Instant.now(), "REJECTED_ENDED");
        }

        if (request.amount().compareTo(auction.getCurrentPrice()) <= 0) {
            return new BidUpdateMessage(auctionId, auction.getCurrentPrice(), request.bidderName(),
                    Instant.now(), "REJECTED_TOO_LOW");
        }

        auction.setCurrentPrice(request.amount());
        auctionRepository.save(auction); // version check happens here

        Bid bid = new Bid(auction, request.bidderName(), request.amount());
        bidRepository.save(bid);

        return new BidUpdateMessage(auctionId, auction.getCurrentPrice(), request.bidderName(),
                bid.getPlacedAt(), "ACCEPTED");
    }
}
