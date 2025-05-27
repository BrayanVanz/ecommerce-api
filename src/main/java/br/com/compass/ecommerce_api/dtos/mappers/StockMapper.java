package br.com.compass.ecommerce_api.dtos.mappers;

import org.modelmapper.ModelMapper;

import br.com.compass.ecommerce_api.dtos.StockResponseDto;
import br.com.compass.ecommerce_api.dtos.StockSaveDto;
import br.com.compass.ecommerce_api.entities.Stock;

public class StockMapper {

    public static Stock toStock(StockSaveDto dto) {
        return new ModelMapper().map(dto, Stock.class);
    }

    public static StockResponseDto tDto(Stock stock) {
        return new ModelMapper().map(stock, StockResponseDto.class);
    }
}
