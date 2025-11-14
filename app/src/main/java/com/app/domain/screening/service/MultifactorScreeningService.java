package com.app.domain.screening.service;


import com.app.domain.screening.dto.ScreeningRequest;
import com.app.domain.screening.dto.ScreeningResponse;
import com.app.domain.screening.dto.ScreeningResultPage;
import com.app.domain.screening.entity.MultifactorScreening;
import com.app.domain.screening.mapper.MultifactorScreeningMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MultifactorScreeningService {

    private final MultifactorScreeningMapper screeningMapper;

    /**
     * 멀티팩터 스크리닝 수행
     */
    @Transactional
    public ScreeningResultPage performScreening(ScreeningRequest request, HttpSession session) {
        String sessionId = session.getId();

        log.info("멀티팩터 스크리닝 시작 - SessionId: {}", sessionId);

        // 세션 존재 확인 및 자동 생성
        ensureSessionExists(sessionId);

        // 가중치 합계 검증
        validateWeights(request);

        // 기존 스크리닝 결과 삭제
        screeningMapper.deleteScreeningResultsBySession(sessionId);

        // 가중치 합계 검증
        validateWeights(request);

        // 기존 스크리닝 결과 삭제
        screeningMapper.deleteScreeningResultsBySession(sessionId);

        // 모든 종목 데이터 조회
        List<MultifactorScreening> allStocks = screeningMapper.selectAllStocksForScreening(request.getMaxDebtRatio());

        log.info("분석 대상 종목 수: {}", allStocks.size());

        // 팩터별 점수 계산 및 순위 매기기
        List<MultifactorScreening> scoredStocks = calculateFactorScores(allStocks, request, sessionId);

        // 복합 점수 계산 및 최종 순위 결정
        List<MultifactorScreening> rankedStocks = calculateCompositeScoresAndRanking(scoredStocks, request);

        // 상위 50개 선별
        markTop50Stocks(rankedStocks);

        // 결과 저장
        screeningMapper.insertScreeningResults(rankedStocks);

        log.info("멀티팩터 스크리닝 완료 - 총 {}개 종목 분석", rankedStocks.size());

        // 첫 페이지 결과 반환
        return getScreeningResults(sessionId, 0, 30, "ranking", "ASC");
    }

    /**
     * 세션이 DB에 없으면 자동 생성 (안전한 방식)
     */
    private void ensureSessionExists(String sessionId) {
        try {
            // 세션 존재 여부 확인
            boolean sessionExists = screeningMapper.checkSessionExists(sessionId);

            if (!sessionExists) {
                // 존재하지 않으면 생성
                screeningMapper.insertUserSession(sessionId, "127.0.0.1", "System");
                log.info("세션 자동 생성 완료: {}", sessionId);
            } else {
                log.debug("세션이 이미 존재함: {}", sessionId);
            }
        } catch (Exception e) {
            log.warn("세션 생성 중 오류 (계속 진행): {}", e.getMessage());
            // 오류가 발생해도 계속 진행 (중복 등의 경우)
        }
    }

    /**
     * 팩터별 점수 계산 (순위 기반 점수화)
     */
    private List<MultifactorScreening> calculateFactorScores(List<MultifactorScreening> stocks,
                                                             ScreeningRequest request, String sessionId) {

        // PER 기준 정렬 (낮을수록 좋음 - 오름차순)
        List<MultifactorScreening> perSorted = new ArrayList<>(stocks);
        perSorted.sort(Comparator.comparing(MultifactorScreening::getPer, Comparator.nullsLast(Comparator.naturalOrder())));

        // PBR 기준 정렬 (낮을수록 좋음 - 오름차순)
        List<MultifactorScreening> pbrSorted = new ArrayList<>(stocks);
        pbrSorted.sort(Comparator.comparing(MultifactorScreening::getPbr, Comparator.nullsLast(Comparator.naturalOrder())));

        // ROE 기준 정렬 (높을수록 좋음 - 내림차순)
        List<MultifactorScreening> roeSorted = new ArrayList<>(stocks);
        roeSorted.sort(Comparator.comparing(MultifactorScreening::getRoe, Comparator.nullsLast(Comparator.reverseOrder())));

        // 각 종목에 대해 순위 기반 점수 부여
        for (int i = 0; i < stocks.size(); i++) {
            MultifactorScreening stock = stocks.get(i);

            // PER 점수 (순위 기반, 1위=1.0, 꼴등=0)
            int perRank = findRankInList(perSorted, stock.getTicker());
            BigDecimal perScore = calculateRankScore(perRank, stocks.size());

            // PBR 점수 (순위 기반, 1위=1.0, 꼴등=0)
            int pbrRank = findRankInList(pbrSorted, stock.getTicker());
            BigDecimal pbrScore = calculateRankScore(pbrRank, stocks.size());

            // ROE 점수 (순위 기반, 1위=1.0, 꼴등=0)
            int roeRank = findRankInList(roeSorted, stock.getTicker());
            BigDecimal roeScore = calculateRankScore(roeRank, stocks.size());

            // 점수 설정
            stock.setPerScore(perScore);
            stock.setPbrScore(pbrScore);
            stock.setRoeScore(roeScore);

            // 가중치 설정
            stock.setPerWeight(request.getPerWeight());
            stock.setPbrWeight(request.getPbrWeight());
            stock.setRoeWeight(request.getRoeWeight());

            // 메타 정보 설정
            stock.setSessionId(sessionId);
            stock.setScreeningDate(LocalDate.now());
            stock.setCreatedAt(LocalDateTime.now());
        }

        return stocks;
    }

    /**
     * 복합 점수 계산 및 최종 순위 결정
     */
    private List<MultifactorScreening> calculateCompositeScoresAndRanking(List<MultifactorScreening> stocks,
                                                                          ScreeningRequest request) {

        // 복합 점수 계산
        for (MultifactorScreening stock : stocks) {
            BigDecimal compositeScore = stock.getPerScore().multiply(request.getPerWeight())
                    .add(stock.getPbrScore().multiply(request.getPbrWeight()))
                    .add(stock.getRoeScore().multiply(request.getRoeWeight()));

            stock.setCompositeScore(compositeScore.setScale(6, RoundingMode.HALF_UP));
        }

        // 복합 점수 기준 정렬 (높은 순)
        stocks.sort(Comparator.comparing(MultifactorScreening::getCompositeScore).reversed());

        // 순위 부여
        IntStream.range(0, stocks.size())
                .forEach(i -> stocks.get(i).setRanking(i + 1));

        return stocks;
    }

    /**
     * 상위 50개 종목 표시
     */
    private void markTop50Stocks(List<MultifactorScreening> rankedStocks) {
        for (int i = 0; i < rankedStocks.size(); i++) {
            rankedStocks.get(i).setIsSelected(i < 50);
        }
    }

    /**
     * 순위 기반 점수 계산 (1위=1.0, 꼴등=0에 가까운 값)
     */
    private BigDecimal calculateRankScore(int rank, int totalCount) {
        if (totalCount <= 1) return BigDecimal.ONE;

        double score = (double) (totalCount - rank) / (totalCount - 1);
        return BigDecimal.valueOf(score).setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * 특정 종목의 순위 찾기
     */
    private int findRankInList(List<MultifactorScreening> sortedList, String ticker) {
        for (int i = 0; i < sortedList.size(); i++) {
            if (sortedList.get(i).getTicker().equals(ticker)) {
                return i;
            }
        }
        return sortedList.size() - 1; // 못 찾으면 꼴등
    }

    /**
     * 가중치 검증
     */
    private void validateWeights(ScreeningRequest request) {
        BigDecimal sum = request.getPerWeight()
                .add(request.getPbrWeight())
                .add(request.getRoeWeight());

        if (sum.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("가중치의 합은 1.0이어야 합니다. 현재 합: " + sum);
        }
    }

    //--------------------------------------------------------------
    /**
     * 스크리닝 결과 조회 (페이징)
     */
    public ScreeningResultPage getScreeningResults(String sessionId, int page, int size, String sortBy, String sortDirection) {
        int offset = page * size;

        List<MultifactorScreening> results = screeningMapper.selectScreeningResults(sessionId, offset, size, sortBy, sortDirection);
        int totalElements = screeningMapper.countScreeningResults(sessionId);

        List<ScreeningResponse> responses = results.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ScreeningResultPage.builder()
                .screeningResults(responses)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .currentPage(page)
                .pageSize(size)
                .hasNext((page + 1) * size < totalElements)
                .hasPrevious(page > 0)
                .totalStocksAnalyzed(totalElements)
                .selectedStocksCount(Math.min(totalElements, 50))
                .sessionId(sessionId)
                .build();
    }

    /**
     * 상위 50개 종목 조회
     */
    public List<ScreeningResponse> getTop50Results(String sessionId) {
        List<MultifactorScreening> top50 = screeningMapper.selectTop50Results(sessionId);
        return top50.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Entity를 Response DTO로 변환
     */
    private ScreeningResponse convertToResponse(MultifactorScreening screening) {
        return ScreeningResponse.builder()
                .screeningId(screening.getScreeningId())
                .ticker(screening.getTicker())
                .stockName(screening.getStockName())
                .industry(screening.getIndustry())
                .per(screening.getPer())
                .pbr(screening.getPbr())
                .roe(screening.getRoe())
                .perScore(screening.getPerScore())
                .pbrScore(screening.getPbrScore())
                .roeScore(screening.getRoeScore())
                .compositeScore(screening.getCompositeScore())
                .ranking(screening.getRanking())
                .isSelected(screening.getIsSelected())
                .closePrice(screening.getClosePrice())
                .debtRatio(screening.getDebtRatio())
                .screeningDate(screening.getScreeningDate())
                .createdAt(screening.getCreatedAt())
                .build();
    }


}//class
