package com.app.domain.screening.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningResultPage {



    private List<ScreeningResponse> screeningResults;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    // 요약 정보
    private int totalStocksAnalyzed;
    private int selectedStocksCount; // 상위 50개
    private String sessionId;



}//class
