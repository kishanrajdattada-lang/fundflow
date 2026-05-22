package com.fundflow.fund;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record BuyFundRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Units are required")
        @Positive(message = "Units must be positive")
        BigDecimal units
) {
}
