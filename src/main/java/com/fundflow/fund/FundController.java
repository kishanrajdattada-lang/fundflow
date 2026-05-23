package com.fundflow.fund;

import com.fundflow.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Funds", description = "Endpoints for managing and purchasing funds")
public class FundController {

    private final FundRepository fundRepository;
    private final FundService fundService;

    @Operation(summary = "List all funds", description = "Returns a list of all available investment funds.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Funds fetched successfully")
    @GetMapping
    public ApiResponse<List<Fund>> listFunds() {
        return ApiResponse.<List<Fund>>builder()
                .status(HttpStatus.OK.value())
                .message("Funds fetched successfully")
                .data(fundRepository.findAll())
                .build();
    }

    @Operation(summary = "Create a new fund", description = "Creates a new investment fund. Requires ADMIN role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        /*@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Fund created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - ISIN already exists")*/
    })
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

    @Operation(summary = "Purchase units of a fund", description = "Allows an investor to buy units of a fund. Requires INVESTOR role.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Fund purchased successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires INVESTOR role"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Fund not found")
    })
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
