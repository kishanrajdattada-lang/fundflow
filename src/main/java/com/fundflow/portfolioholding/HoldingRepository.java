package com.fundflow.portfolioholding;

import com.fundflow.fund.Fund;
import com.fundflow.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HoldingRepository extends JpaRepository<PortfolioHolding, UUID> {
    Optional<PortfolioHolding> findByUserAndFund(User user, Fund fund);
}
