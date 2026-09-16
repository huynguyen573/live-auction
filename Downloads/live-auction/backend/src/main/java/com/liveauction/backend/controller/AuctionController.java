package com.liveauction.backend.controller;

import com.liveauction.backend.dto.CreateAuctionRequest;
import com.liveauction.backend.model.Auction;
import com.liveauction.backend.model.Bid;
import com.liveauction.backend.repository.AuctionRepository;
import com.liveauction.backend.repository.BidRepository;
import com.liveauction.backend.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuctionService auctionService;

    public AuctionController(AuctionRepository auctionRepository, BidRepository bidRepository,
                             AuctionService auctionService) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.auctionService = auctionService;
    }

    @GetMapping
    public List<Auction> listAuctions() {
        return auctionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuction(@PathVariable Long id) {
        return auctionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/bids")
    public List<Bid> getBidHistory(@PathVariable Long id) {
        return bidRepository.findByAuctionIdOrderByPlacedAtDesc(id);
    }

    @PostMapping
    public Auction createAuction(@Valid @RequestBody CreateAuctionRequest request) {
        return auctionService.createAuction(request);
    }
}
