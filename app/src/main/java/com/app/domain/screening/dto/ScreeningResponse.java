package com.app.domain.screening.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningResponse {

    private Long screeningId;
    private String ticker;
    private String stockName;
    private String industry;
    private BigDecimal per;
    private BigDecimal pbr;
    private BigDecimal roe;
    private BigDecimal perScore;
    private BigDecimal pbrScore;
    private BigDecimal roeScore;
    private BigDecimal compositeScore;
    private Integer ranking;
    private Boolean isSelected;
    private BigDecimal closePrice;
    private BigDecimal debtRatio;
    private LocalDate screeningDate;
    private LocalDateTime createdAt;





}//class
