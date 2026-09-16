package com.liveauction.backend.config;

import com.liveauction.backend.model.Auction;
import com.liveauction.backend.repository.AuctionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
public class DataSeeder {

    @org.springframework.context.annotation.Bean
    CommandLineRunner seed(AuctionRepository auctionRepository) {
        return args -> {
            if (auctionRepository.count() == 0) {
                auctionRepository.save(new Auction(
                        "Vintage Mechanical Keyboard",
                        "1980s IBM Model M, fully refurbished.",
                        new BigDecimal("50.00"),
                        Instant.now().plus(1, ChronoUnit.DAYS)
                ));
                auctionRepository.save(new Auction(
                        "Signed First Edition Novel",
                        "Rare signed copy, mint condition.",
                        new BigDecimal("120.00"),
                        Instant.now().plus(2, ChronoUnit.DAYS)
                ));
            }
        };
    }
}
