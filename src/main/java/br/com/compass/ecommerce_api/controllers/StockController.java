package br.com.compass.ecommerce_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import br.com.compass.ecommerce_api.dtos.StockAlterDto;
import br.com.compass.ecommerce_api.dtos.StockResponseDto;
import br.com.compass.ecommerce_api.dtos.StockSaveDto;
import br.com.compass.ecommerce_api.dtos.mappers.PageableMapper;
import br.com.compass.ecommerce_api.dtos.mappers.StockMapper;
import br.com.compass.ecommerce_api.entities.Stock;
import br.com.compass.ecommerce_api.exceptions.ErrorMessage;
import br.com.compass.ecommerce_api.projections.StockProjection;
import br.com.compass.ecommerce_api.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Stock", description = "Performs stock related operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stock")
@PreAuthorize("hasAuthority('ADMIN')")
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Creates a new stock entry", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Product already registered in the stock",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping
    public ResponseEntity<StockResponseDto> save(@Valid @RequestBody StockSaveDto dto) {
        Stock stock = stockService.save(dto.getProductId(), StockMapper.toStock(dto));
        StockResponseDto responseDto = StockMapper.tDto(stock);
        responseDto.setProductId(stock.getProduct().getId());
        responseDto.setProductName(stock.getProduct().getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "Retrieves a stock entry by its id", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retireved successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDto> findById(@PathVariable Long id) {
        Stock stock = stockService.findById(id);
        StockResponseDto responseDto = StockMapper.tDto(stock);
        responseDto.setProductId(stock.getProduct().getId());
        responseDto.setProductName(stock.getProduct().getName());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Adds quantity to stock entry", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Stock updated successfully"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/add-stock/{id}")
    public ResponseEntity<Void> add(@PathVariable Long id, @Valid @RequestBody StockAlterDto dto) {
        stockService.add(id, dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Decreases quantity to stock entry", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        responses = {
            @ApiResponse(responseCode = "204", description = "Stock updated successfully"), 
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Stock quantity can't be negative",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PatchMapping("/decrease-stock/{id}")
    public ResponseEntity<Void> decrease(@PathVariable Long id, @Valid @RequestBody StockAlterDto dto) {
        stockService.decrease(id, dto.getQuantity());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retrieves entries with quantity below 10", description = "Requires Bearer Token. Access restricted to ADMIN", 
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
                array = @ArraySchema(schema = @Schema(implementation = StockProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping("/low-stock")
    public ResponseEntity<PageableDto<StockProjection>> findLowStock(@PageableDefault(size = 3) Pageable pageable) {
        Page<StockProjection> lowStockItems = stockService.findLowStock(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(lowStockItems));
    }

    @Operation(summary = "Retrieves all stock entries", description = "Requires Bearer Token. Access restricted to ADMIN", 
        security = @SecurityRequirement(name = "security"),
        parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "page",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "Represents a returned page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "size",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "3")),
                description = "Reperesents the amount of elements in a page"
            ),
            @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                description = "Represents the sorting type being used. Accepts multiple criteria"
            )
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Resource retrieved successfully", 
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = StockProjection.class)))
            ),
            @ApiResponse(responseCode = "403", description = "User doesn't have permission to access resource",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping
    public ResponseEntity<PageableDto<StockProjection>> findAll(@PageableDefault(size = 3) Pageable pageable) {
        Page<StockProjection> stocks = stockService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(stocks));
    }
}
