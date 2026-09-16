package com.liveauction.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal startingPrice;

    @Column(nullable = false)
    private BigDecimal currentPrice;

    @Column(nullable = false)
    private Instant endsAt;

    /**
     * Optimistic locking: every UPDATE checks this value against the DB row.
     * If another transaction bumped it first, JPA throws
     * OptimisticLockingFailureException instead of silently overwriting a bid.
     * This is the core concurrency-safety mechanism for the bidding flow.
     */
    @Version
    private Long version;

    protected Auction() {
        // JPA
    }

    public Auction(String title, String description, BigDecimal startingPrice, Instant endsAt) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.endsAt = endsAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getStartingPrice() { return startingPrice; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public Instant getEndsAt() { return endsAt; }
    public Long getVersion() { return version; }
}
