package com.liveauction.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Column(nullable = false)
    private String bidderName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant placedAt;

    protected Bid() {
        // JPA
    }

    public Bid(Auction auction, String bidderName, BigDecimal amount) {
        this.auction = auction;
        this.bidderName = bidderName;
        this.amount = amount;
        this.placedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Auction getAuction() { return auction; }
    public String getBidderName() { return bidderName; }
    public BigDecimal getAmount() { return amount; }
    public Instant getPlacedAt() { return placedAt; }
}
