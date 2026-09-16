package com.liveauction.backend.controller;

import com.liveauction.backend.dto.BidRequest;
import com.liveauction.backend.dto.BidUpdateMessage;
import com.liveauction.backend.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Handles bids sent by clients over the WebSocket connection and broadcasts
 * the outcome to every subscriber of that auction's topic in real time.
 *
 * Client sends to:      /app/auctions/{auctionId}/bid
 * Server broadcasts on: /topic/auctions/{auctionId}
 */
@Controller
public class BidWebSocketController {

    private final AuctionService auctionService;
    private final SimpMessagingTemplate messagingTemplate;

    public BidWebSocketController(AuctionService auctionService, SimpMessagingTemplate messagingTemplate) {
        this.auctionService = auctionService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/auctions/{auctionId}/bid")
    public void handleBid(@DestinationVariable Long auctionId, @Valid @Payload BidRequest bidRequest) {
        BidUpdateMessage result = auctionService.placeBid(auctionId, bidRequest);
        messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, result);
    }
}
