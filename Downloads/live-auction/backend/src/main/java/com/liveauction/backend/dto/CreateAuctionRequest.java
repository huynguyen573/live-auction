package com.liveauction.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateAuctionRequest(
        @NotBlank String title,
        String description,
        @DecimalMin(value = "0.01") BigDecimal startingPrice,
        @Future Instant endsAt
) {
}
