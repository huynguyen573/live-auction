package com.liveauction.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record BidRequest(
        @NotBlank String bidderName,
        @DecimalMin(value = "0.01") BigDecimal amount
) {
}
