package com.liveauction.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * What gets pushed to every client subscribed to /topic/auctions/{auctionId}
 * whenever a new bid is accepted.
 */
public record BidUpdateMessage(
        Long auctionId,
        BigDecimal currentPrice,
        String bidderName,
        Instant placedAt,
        String status // "ACCEPTED" or "REJECTED"
) {
}
