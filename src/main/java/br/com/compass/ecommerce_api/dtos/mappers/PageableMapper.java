package br.com.compass.ecommerce_api.dtos.mappers;

import org.springframework.data.domain.Page;

import br.com.compass.ecommerce_api.dtos.PageableDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PageableMapper {

    public static <T> PageableDto<T> toDto(Page<T> page) {
        PageableDto<T> dto = new PageableDto<>();
        dto.setContent(page.getContent());
        dto.setFirst(page.isFirst());
        dto.setLast(page.isLast());
        dto.setNumber(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        return dto;
    }
}
