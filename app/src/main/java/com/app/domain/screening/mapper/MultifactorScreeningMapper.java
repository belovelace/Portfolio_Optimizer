package com.app.domain.screening.mapper;


import com.app.domain.screening.entity.MultifactorScreening;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MultifactorScreeningMapper {

    /**
     * 세션 존재 여부 확인
     */
    boolean checkSessionExists(@Param("sessionId") String sessionId);

    /**
     * 사용자 세션 생성
     */
    void insertUserSession(@Param("sessionId") String sessionId,
                           @Param("userIp") String userIp,
                           @Param("userAgent") String userAgent);

    /**
     * 모든 종목의 팩터 데이터 조회 (스크리닝용)
     */
    List<MultifactorScreening> selectAllStocksForScreening(@Param("maxDebtRatio") BigDecimal maxDebtRatio);

    /**
     * 스크리닝 결과 일괄 저장
     */
    void insertScreeningResults(@Param("results") List<MultifactorScreening> results);

    /**
     * 기존 스크리닝 결과 삭제 (세션별)
     */
    void deleteScreeningResultsBySession(@Param("sessionId") String sessionId);

    /**
     * 스크리닝 결과 조회 (페이징)
     */
    List<MultifactorScreening> selectScreeningResults(
            @Param("sessionId") String sessionId,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection
    );

    /**
     * 스크리닝 결과 총 개수 조회
     */
    int countScreeningResults(@Param("sessionId") String sessionId);

    /**
     * 상위 50개 선별된 종목 조회
     */
    List<MultifactorScreening> selectTop50Results(@Param("sessionId") String sessionId);




}//interface
