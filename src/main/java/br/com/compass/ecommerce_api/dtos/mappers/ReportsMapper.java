package br.com.compass.ecommerce_api.dtos.mappers;

import java.math.BigDecimal;

import br.com.compass.ecommerce_api.dtos.TotalAmountReportDto;
import br.com.compass.ecommerce_api.dtos.TotalPurchasesReportDto;

public class ReportsMapper {

    public static TotalAmountReportDto toTotalAmountDto(String period, BigDecimal amount) {
        return new TotalAmountReportDto(period, amount);
    }

    public static TotalPurchasesReportDto toTotalPurchasesDto(String period, Integer count) {
        return new TotalPurchasesReportDto(period, count);
    }
}
