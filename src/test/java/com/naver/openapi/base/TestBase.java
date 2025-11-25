package com.naver.openapi.base;

import com.naver.openapi.client.NaverApiClient;
import com.naver.openapi.config.ApiConfig;
import com.naver.openapi.config.Environment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.Instant;

/**
 * 모든 테스트의 베이스 클래스
 * 테스트 환경 초기화 및 공통 기능 제공
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TestBase {
    
    protected NaverApiClient naverApiClient;
    protected ApiConfig apiConfig;
    protected Environment environment;
    
    private Instant testStartTime;
    
    /**
     * 전체 테스트 클래스 실행 전 1회 실행
     */
    @BeforeAll
    void setUpAll() {
        log.info("========================================");
        log.info("Test Suite Started: {}", this.getClass().getSimpleName());
        log.info("========================================");
        
        // 환경 설정 로드
        initializeEnvironment();
        
        // API 클라이언트 초기화
        initializeApiClient();
        
        log.info("Environment: {}", environment.getDescription());
        log.info("Base URL: {}", apiConfig.getBaseUrl());
        log.info("========================================");
    }
    
    /**
     * 각 테스트 메서드 실행 전 실행
     */
    @BeforeEach
    void setUp(TestInfo testInfo) {
        testStartTime = Instant.now();
        log.info("▶ Starting Test: {} - {}",
                testInfo.getTestClass().orElseThrow().getSimpleName(),
                testInfo.getDisplayName());
    }
    
    /**
     * 각 테스트 메서드 실행 후 실행
     */
    @AfterEach
    void tearDown(TestInfo testInfo) {
        Duration duration = Duration.between(testStartTime, Instant.now());
        log.info("◀ Completed Test: {} - {} (Duration: {}ms)",
                testInfo.getTestClass().orElseThrow().getSimpleName(),
                testInfo.getDisplayName(),
                duration.toMillis());
        log.info("----------------------------------------");
    }
    
    /**
     * 전체 테스트 클래스 실행 후 1회 실행
     */
    @AfterAll
    void tearDownAll() {
        log.info("========================================");
        log.info("Test Suite Completed: {}", this.getClass().getSimpleName());
        log.info("========================================");
    }
    
    /**
     * 환경 설정 초기화
     */
    private void initializeEnvironment() {
        try {
            // 시스템 프로퍼티에서 환경 정보 로드
            apiConfig = ApiConfig.fromSystemProperty(true);
            environment = apiConfig.getEnvironment();
            
            log.info("Successfully initialized environment: {}", environment.getName());
        } catch (Exception e) {
            log.error("Failed to initialize environment", e);
            throw new RuntimeException("Environment initialization failed", e);
        }
    }
    
    /**
     * API 클라이언트 초기화
     */
    private void initializeApiClient() {
        try {
            naverApiClient = new NaverApiClient(apiConfig);
            log.info("Successfully initialized Naver API Client");
        } catch (Exception e) {
            log.error("Failed to initialize API Client", e);
            throw new RuntimeException("API Client initialization failed", e);
        }
    }
    
    /**
     * 테스트 환경 검증
     */
    protected void validateEnvironment(Environment expectedEnvironment) {
        if (environment != expectedEnvironment) {
            throw new IllegalStateException(
                String.format("Expected environment: %s, but got: %s",
                    expectedEnvironment.getName(), environment.getName())
            );
        }
    }
    
    /**
     * 테스트 대기 (Rate Limit 방지)
     */
    protected void waitForRateLimit(long milliseconds) {
        try {
            log.debug("Waiting for {} ms to avoid rate limit", milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Wait interrupted", e);
        }
    }
    
    /**
     * 로그 구분선 출력
     */
    protected void logSeparator() {
        log.info("========================================");
    }
    
    /**
     * 로그 구분선 출력 (작은 크기)
     */
    protected void logMinorSeparator() {
        log.info("----------------------------------------");
    }
}
