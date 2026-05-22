package com.fundflow.fund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateFundRequest(
        @NotBlank(message = "Fund name is required")
        String name,

        @NotNull(message = "Fund category is required")
        FundCategory category,

        @NotBlank(message = "Fund ISIN is required")
        String isin,

        @NotBlank(message = "Fund currency is required")
        String currency,

        @NotNull(message = "Fund NAV is required")
        @Positive(message = "Fund NAV must be positive")
        BigDecimal nav
) {
}
