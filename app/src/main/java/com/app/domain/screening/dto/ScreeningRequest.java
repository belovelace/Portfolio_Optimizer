package com.app.domain.screening.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRequest {



    @NotNull(message = "PER 가중치는 필수입니다.")
    @DecimalMin(value = "0.0", message = "PER 가중치는 0 이상이어야 합니다.")
    @DecimalMax(value = "1.0", message = "PER 가중치는 1 이하여야 합니다.")
    private BigDecimal perWeight;

    @NotNull(message = "PBR 가중치는 필수입니다.")
    @DecimalMin(value = "0.0", message = "PBR 가중치는 0 이상이어야 합니다.")
    @DecimalMax(value = "1.0", message = "PBR 가중치는 1 이하여야 합니다.")
    private BigDecimal pbrWeight;

    @NotNull(message = "ROE 가중치는 필수입니다.")
    @DecimalMin(value = "0.0", message = "ROE 가중치는 0 이상이어야 합니다.")
    @DecimalMax(value = "1.0", message = "ROE 가중치는 1 이하여야 합니다.")
    private BigDecimal roeWeight;

    @DecimalMin(value = "0.0", message = "최대 부채비율은 0 이상이어야 합니다.")
    private BigDecimal maxDebtRatio = new BigDecimal("2.0"); // 기본값: 200%







}//class
