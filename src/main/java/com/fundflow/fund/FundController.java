package com.fundflow.fund;

import com.fundflow.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundRepository fundRepository;
    private final FundService fundService;

    @GetMapping
    public ApiResponse<List<Fund>> listFunds() {
        return ApiResponse.<List<Fund>>builder()
                .status(HttpStatus.OK.value())
                .message("Funds fetched successfully")
                .data(fundRepository.findAll())
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Fund> createFund(@Valid @RequestBody CreateFundRequest request) {
        if (fundRepository.existsByIsin(request.isin().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fund ISIN already exists");
        }

        Fund fund = new Fund(
                request.name().trim(),
                request.category(),
                request.isin().trim(),
                request.currency().trim(),
                request.nav()
        );
        Fund savedFund = fundRepository.save(fund);

        return ApiResponse.<Fund>builder()
                .status(HttpStatus.CREATED.value())
                .message("Fund created successfully")
                .data(savedFund)
                .build();
    }

    @PostMapping("/{fundId}/buy")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INVESTOR')")
    public ApiResponse<BuyFundResponse> buyFund(@PathVariable UUID fundId, @Valid @RequestBody BuyFundRequest request) {
        BuyFundResponse response = fundService.buyFund(fundId, request);
        return ApiResponse.<BuyFundResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Fund purchased successfully")
                .data(response)
                .build();
    }
}
