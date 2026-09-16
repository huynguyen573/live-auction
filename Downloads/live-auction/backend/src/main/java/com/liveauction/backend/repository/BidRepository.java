package com.liveauction.backend.repository;

import com.liveauction.backend.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionIdOrderByPlacedAtDesc(Long auctionId);
}
