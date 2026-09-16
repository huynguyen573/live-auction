package com.liveauction.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Enables STOMP messaging over WebSocket (with SockJS fallback for browsers/networks
 * that block raw WebSocket connections).
 *
 * Clients CONNECT to /ws, then:
 *  - SUBSCRIBE to /topic/auctions/{auctionId}  -> receive live bid updates for that auction
 *  - SEND to /app/auctions/{auctionId}/bid     -> place a bid
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // tighten this before deploying publicly
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages the server pushes OUT to subscribed clients
        registry.enableSimpleBroker("/topic");
        // Messages clients send IN to the server (routed to @MessageMapping methods)
        registry.setApplicationDestinationPrefixes("/app");
    }
}
