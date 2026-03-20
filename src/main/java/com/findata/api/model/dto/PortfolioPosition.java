package com.findata.api.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioPosition {

    @NotBlank(message = "Ticker is required")
    private String ticker;

    @DecimalMin(value = "0.0", message = "Weight must be greater than or equal to 0")
    @DecimalMax(value = "1.0", message = "Weight must be less than or equal to 1")
    private BigDecimal weight;
}
