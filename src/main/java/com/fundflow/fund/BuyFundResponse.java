package com.fundflow.fund;

import java.math.BigDecimal;
import java.util.UUID;

public record BuyFundResponse(
        UUID transactionId,
        UUID holdingId,
        UUID userId,
        UUID fundId,
        BigDecimal purchasedUnits,
        BigDecimal navAtPurchase,
        BigDecimal amount,
        BigDecimal totalHoldingUnits,
        BigDecimal averageNav
) {
}
