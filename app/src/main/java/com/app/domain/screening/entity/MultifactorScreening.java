package com.app.domain.screening.entity;




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
public class MultifactorScreening {


    private Long screeningId;           // 스크리닝 ID (PK)
    private String sessionId;           // 세션 ID
    private String ticker;              // 티커 심볼
    private BigDecimal perScore;        // PER 점수
    private BigDecimal pbrScore;        // PBR 점수
    private BigDecimal roeScore;        // ROE 점수
    private BigDecimal perWeight;       // PER 가중치
    private BigDecimal pbrWeight;       // PBR 가중치
    private BigDecimal roeWeight;       // ROE 가중치
    private BigDecimal compositeScore;  // 종합 점수
    private Integer ranking;            // 순위
    private Boolean isSelected;         // 상위 50개 선별 여부
    private LocalDate screeningDate;    // 스크리닝 일자
    private LocalDateTime createdAt;    // 생성일시

    // 조인용 주식 정보
    private String stockName;           // 종목명
    private String industry;            // 업종
    private BigDecimal per;             // PER
    private BigDecimal pbr;             // PBR
    private BigDecimal roe;             // ROE
    private BigDecimal closePrice;      // 종가
    private BigDecimal debtRatio;       // 부채비율









}//class
