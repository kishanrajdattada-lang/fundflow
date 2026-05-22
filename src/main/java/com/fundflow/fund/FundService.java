package com.fundflow.fund;

import com.fundflow.portfolioholding.HoldingRepository;
import com.fundflow.portfolioholding.PortfolioHolding;
import com.fundflow.transaction.Transaction;
import com.fundflow.transaction.TransactionType;
import com.fundflow.transaction.TxRepository;
import com.fundflow.user.User;
import com.fundflow.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
public class FundService {
    private static final int MONEY_SCALE = 4;

    private final FundRepository fundRepository;
    private final UserRepo userRepo;
    private final HoldingRepository holdingRepository;
    private final TxRepository txRepository;

    public FundService(
            FundRepository fundRepository,
            UserRepo userRepo,
            HoldingRepository holdingRepository,
            TxRepository txRepository
    ) {
        this.fundRepository = fundRepository;
        this.userRepo = userRepo;
        this.holdingRepository = holdingRepository;
        this.txRepository = txRepository;
    }

    @Transactional
    public BuyFundResponse buyFund(UUID fundId, BuyFundRequest request) {
        if (request.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }
        if (request.units() == null || request.units().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Units must be greater than zero");
        }

        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fund not found"));

        BigDecimal purchasedUnits = request.units();
        BigDecimal navAtPurchase = fund.getNav();
        BigDecimal amount = navAtPurchase.multiply(purchasedUnits).setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        PortfolioHolding holding = holdingRepository.findByUserAndFund(user, fund)
                .map(existingHolding -> updateExistingHolding(existingHolding, purchasedUnits, navAtPurchase))
                .orElseGet(() -> new PortfolioHolding(user, fund, purchasedUnits, navAtPurchase, Instant.now()));

        PortfolioHolding savedHolding = holdingRepository.save(holding);
        Transaction savedTransaction = txRepository.save(new Transaction(
                user,
                fund,
                TransactionType.BUY,
                purchasedUnits,
                navAtPurchase,
                amount
        ));

        return new BuyFundResponse(
                savedTransaction.getId(),
                savedHolding.getId(),
                user.getId(),
                fund.getId(),
                purchasedUnits,
                navAtPurchase,
                amount,
                savedHolding.getUnits(),
                savedHolding.getAverageNav()
        );
    }

    private PortfolioHolding updateExistingHolding(
            PortfolioHolding holding,
            BigDecimal purchasedUnits,
            BigDecimal navAtPurchase
    ) {
        BigDecimal currentUnits = holding.getUnits();
        BigDecimal totalUnits = currentUnits.add(purchasedUnits);
        BigDecimal currentCost = currentUnits.multiply(holding.getAverageNav());
        BigDecimal purchaseCost = purchasedUnits.multiply(navAtPurchase);
        BigDecimal averageNav = currentCost.add(purchaseCost)
                .divide(totalUnits, MONEY_SCALE, RoundingMode.HALF_UP);

        holding.setUnits(totalUnits);
        holding.setAverageNav(averageNav);
        holding.setUpdatedAt(Instant.now());
        return holding;
    }
}
