package br.com.compass.ecommerce_api.controllers;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.TotalAmountReportDto;
import br.com.compass.ecommerce_api.dtos.TotalPurchasesReportDto;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.dtos.mappers.ReportsMapper;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;
import br.com.compass.ecommerce_api.projections.TopBuyerProjection;
import br.com.compass.ecommerce_api.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Purchases", description = "Performs purchase related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Retrieves total profit", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "period",
                content = @Content(schema = @Schema(type = "string")),
                description = "Represent time a time period. It can be 'day', 'week' or 'month'")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TotalAmountReportDto.class))), 
            @ApiResponse(responseCode = "400", description = "Invalid parameter value",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
        }
    )
    @GetMapping("/total-amount")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TotalAmountReportDto> getTotalAmount(@RequestParam String period) {
        BigDecimal totalAmount = purchaseService.getTotalAmount(period);
        return ResponseEntity.ok(ReportsMapper.toTotalAmountDto(period, totalAmount));
    }

    @Operation(summary = "Retrieves total number of purchases", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
                @Parameter(in = ParameterIn.QUERY, name = "period",
                content = @Content(schema = @Schema(type = "string")),
                description = "Represent time a time period. It can be 'day', 'week' or 'month'")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TotalPurchasesReportDto.class))), 
            @ApiResponse(responseCode = "400", description = "Invalid parameter value",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
        }
    )
    @GetMapping("/total-purchases")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TotalPurchasesReportDto> getTotalPurchases(@RequestParam String period) {
        Integer totalPurchases = purchaseService.getTotalPurchases(period);
        return ResponseEntity.ok(ReportsMapper.toTotalPurchasesDto(period, totalPurchases));
    }

    @Operation(summary = "Performs purchase", description = "Requires Bearer Token. Restricted to own cart", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Product added successfully"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Cart is empty or insufficient stock",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
        }
    )
    @PostMapping("/perform-purchase/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CLIENT') AND #id == authentication.principal.id")
    public ResponseEntity<Void> performPurchase(@PathVariable Long id) {
        purchaseService.performPurchase(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retrieves top buyers", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "Represents a returned page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "size",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "3")),
                description = "Reperesents the amount of elements in a page"
            )
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully", 
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = TopBuyerProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/top-buyers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto<TopBuyerProjection>> findTopBuyers(@PageableDefault(size = 3) Pageable pageable) {
        Page<TopBuyerProjection> topBuyers = purchaseService.findTopBuyers(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(topBuyers));
    }
}
