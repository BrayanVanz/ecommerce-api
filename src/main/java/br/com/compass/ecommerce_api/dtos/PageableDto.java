package br.com.compass.ecommerce_api.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageableDto<T> {

    private List<T> content = new ArrayList<>();
    private boolean first;
    private boolean last;

    @JsonProperty("page")
    private int number;
    private int size;
    
    @JsonProperty("pageElements")
    private int numberOfElements;
    private int totalPages;
    private long totalElements;
}
