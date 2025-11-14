package com.app.domain.screening.controller;


import com.app.domain.screening.dto.ScreeningRequest;
import com.app.domain.screening.dto.ScreeningResponse;
import com.app.domain.screening.dto.ScreeningResultPage;
import com.app.domain.screening.service.MultifactorScreeningService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ScreeningController {


    private final MultifactorScreeningService screeningService;

    /**
     * 멀티팩터 스크리닝 수행
     */
    @PostMapping("/perform")
    public ResponseEntity<ScreeningResultPage> performScreening(
            @Valid @RequestBody ScreeningRequest request,
            HttpSession session) {

        log.info("멀티팩터 스크리닝 요청 - PER가중치: {}, PBR가중치: {}, ROE가중치: {}",
                request.getPerWeight(), request.getPbrWeight(), request.getRoeWeight());

        try {
            ScreeningResultPage result = screeningService.performScreening(request, session);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("스크리닝 파라미터 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("멀티팩터 스크리닝 수행 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 기본 가중치로 스크리닝 수행
     */
    @PostMapping("/perform-default")
    public ResponseEntity<ScreeningResultPage> performDefaultScreening(HttpSession session) {

        ScreeningRequest defaultRequest = new ScreeningRequest(
                new BigDecimal("0.3333"), // PER 가중치
                new BigDecimal("0.3333"), // PBR 가중치
                new BigDecimal("0.3334"), // ROE 가중치
                new BigDecimal("2.0")     // 최대 부채비율
        );

        return performScreening(defaultRequest, session);
    }

    /**
     * 스크리닝 결과 조회 (페이징)
     */
    @GetMapping("/results")
    public ResponseEntity<ScreeningResultPage> getScreeningResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "ranking") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            HttpSession session) {

        try {
            String sessionId = session.getId();
            ScreeningResultPage result = screeningService.getScreeningResults(sessionId, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("스크리닝 결과 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 상위 50개 종목 조회
     */
    @GetMapping("/top50")
    public ResponseEntity<List<ScreeningResponse>> getTop50Results(HttpSession session) {
        try {
            String sessionId = session.getId();
            List<ScreeningResponse> top50 = screeningService.getTop50Results(sessionId);
            return ResponseEntity.ok(top50);
        } catch (Exception e) {
            log.error("상위 50개 종목 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }








}//class
