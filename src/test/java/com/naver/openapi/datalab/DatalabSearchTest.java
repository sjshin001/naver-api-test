package com.naver.openapi.datalab;

import com.naver.openapi.base.TestBase;
import com.naver.openapi.model.request.SearchTrendRequest;
import com.naver.openapi.model.response.SearchTrendResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

/**
 * Naver DataLab Search Trend API 테스트
 * 
 * API 문서: https://developers.naver.com/docs/serviceapi/datalab/search/search.md
 */
@Slf4j
@DisplayName("Naver DataLab Search Trend API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatalabSearchTest extends TestBase {

    private static final String DATALAB_SEARCH_PATH = "/v1/datalab/search";

    @Nested
    @DisplayName("검색어 트렌드 API 테스트")
    class SearchTrendTests {

        @Test
        @Order(1)
        @DisplayName("검색어 트렌드 - 단일 키워드 (월간)")
        void testSearchTrend_SingleKeyword_Monthly() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("자바")
                                    .keywords(List.of("자바", "java"))
                                    .build()
                    ))
                    .build();

            // When & Then - REST Assured 방식
            Response response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("startDate", equalTo("2024-01-01"))
                    .body("endDate", equalTo("2024-12-31"))
                    .body("timeUnit", equalTo("month"))
                    .body("results", hasSize(1))
                    .body("results[0].title", equalTo("자바"))
                    .body("results[0].keywords", hasSize(2))
                    .body("results[0].data", not(empty()))
                    .extract()
                    .response();

            // DTO 변환 및 추가 검증
            SearchTrendResponse trendResponse = response.as(SearchTrendResponse.class);
            
            assertThat(trendResponse).isNotNull();
            assertThat(trendResponse.getResults()).hasSize(1);
            
            SearchTrendResponse.TrendResult result = trendResponse.getResults().get(0);
            assertThat(result.getTitle()).isEqualTo("자바");
            assertThat(result.getKeywords()).containsExactly("자바", "java");
            assertThat(result.getData()).isNotEmpty();
            
            // 데이터 검증
            result.getData().forEach(data -> {
                assertThat(data.getPeriod()).matches("\\d{4}-\\d{2}-\\d{2}");
                assertThat(data.getRatio()).isBetween(0.0, 100.0);
            });
            
            log.info("Total data points: {}", result.getData().size());
        }

        @Test
        @Order(2)
        @DisplayName("검색어 트렌드 - 키워드 비교 (여러 그룹)")
        void testSearchTrend_MultipleKeywords_Comparison() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("자바")
                                    .keywords(List.of("자바", "java"))
                                    .build(),
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("파이썬")
                                    .keywords(List.of("파이썬", "python"))
                                    .build(),
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("자바스크립트")
                                    .keywords(List.of("자바스크립트", "javascript"))
                                    .build()
                    ))
                    .build();

            // When
            SearchTrendResponse response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(200)
                    .body("results", hasSize(3))
                    .extract()
                    .as(SearchTrendResponse.class);

            // Then
            assertThat(response.getResults()).hasSize(3);
            
            // 각 그룹 검증
            List<String> groupNames = response.getResults().stream()
                    .map(SearchTrendResponse.TrendResult::getTitle)
                    .toList();
            
            assertThat(groupNames).containsExactlyInAnyOrder("자바", "파이썬", "자바스크립트");
            
            log.info("Compared keywords: {}", groupNames);
        }

        @Test
        @Order(3)
        @DisplayName("검색어 트렌드 - 주간 단위")
        void testSearchTrend_Weekly() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-11-01")
                    .endDate("2024-11-30")
                    .timeUnit("week")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("맛집")
                                    .keywords(List.of("맛집", "restaurant"))
                                    .build()
                    ))
                    .build();

            // When
            SearchTrendResponse response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(200)
                    .body("timeUnit", equalTo("week"))
                    .extract()
                    .as(SearchTrendResponse.class);

            // Then
            assertThat(response.getTimeUnit()).isEqualTo("week");
            assertThat(response.getResults().get(0).getData()).isNotEmpty();
            
            log.info("Weekly data points: {}", response.getResults().get(0).getData().size());
        }

        @Test
        @Order(4)
        @DisplayName("검색어 트렌드 - 일간 단위")
        void testSearchTrend_Daily() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-11-01")
                    .endDate("2024-11-30")
                    .timeUnit("date")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("테스트자동화")
                                    .keywords(List.of("테스트 자동화", "QA 자동화"))
                                    .build()
                    ))
                    .build();

            // When
            SearchTrendResponse response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(200)
                    .body("timeUnit", equalTo("date"))
                    .extract()
                    .as(SearchTrendResponse.class);

            // Then
            assertThat(response.getTimeUnit()).isEqualTo("date");
            assertThat(response.getResults().get(0).getData()).hasSize(30); // 11월 30일
            
            log.info("Daily data points: {}", response.getResults().get(0).getData().size());
        }

        @Test
        @Order(5)
        @DisplayName("검색어 트렌드 - 디바이스별 (PC)")
        void testSearchTrend_DevicePC() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .device("pc")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("쇼핑")
                                    .keywords(List.of("쇼핑", "shopping"))
                                    .build()
                    ))
                    .build();

            // When
            SearchTrendResponse response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(SearchTrendResponse.class);

            // Then
            assertThat(response.getResults()).hasSize(1);
            log.info("PC search trend retrieved successfully");
        }

        @Test
        @Order(6)
        @DisplayName("검색어 트렌드 - 디바이스별 (모바일)")
        void testSearchTrend_DeviceMobile() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .device("mo")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("배달")
                                    .keywords(List.of("배달", "delivery"))
                                    .build()
                    ))
                    .build();

            // When
            SearchTrendResponse response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(SearchTrendResponse.class);

            // Then
            assertThat(response.getResults()).hasSize(1);
            log.info("Mobile search trend retrieved successfully");
        }

        @Test
        @Order(7)
        @DisplayName("검색어 트렌드 - 성별/연령 필터")
        void testSearchTrend_GenderAgeFilter() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .gender("f")  // 여성
                    .ages(List.of("4", "5", "6"))  // 25~39세
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("화장품")
                                    .keywords(List.of("화장품", "cosmetics"))
                                    .build()
                    ))
                    .build();

            // When
            SearchTrendResponse response = naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(SearchTrendResponse.class);

            // Then
            assertThat(response.getResults()).hasSize(1);
            log.info("Gender/Age filtered trend retrieved successfully");
        }
    }

    @Nested
    @DisplayName("에러 케이스 테스트")
    class ErrorCaseTests {

        @Test
        @Order(10)
        @DisplayName("에러 - 잘못된 날짜 형식")
        void testSearchTrend_InvalidDateFormat() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024/01/01")  // 잘못된 형식
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("테스트")
                                    .keywords(List.of("테스트"))
                                    .build()
                    ))
                    .build();

            // When & Then
            naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(500)));
        }

        @Test
        @Order(11)
        @DisplayName("에러 - 빈 키워드 그룹")
        void testSearchTrend_EmptyKeywordGroups() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("month")
                    .keywordGroups(List.of())  // 빈 그룹
                    .build();

            // When & Then
            naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(500)));
        }

        @Test
        @Order(12)
        @DisplayName("에러 - 잘못된 timeUnit")
        void testSearchTrend_InvalidTimeUnit() {
            // Given
            SearchTrendRequest request = SearchTrendRequest.builder()
                    .startDate("2024-01-01")
                    .endDate("2024-12-31")
                    .timeUnit("year")  // 지원하지 않는 단위
                    .keywordGroups(List.of(
                            SearchTrendRequest.KeywordGroup.builder()
                                    .groupName("테스트")
                                    .keywords(List.of("테스트"))
                                    .build()
                    ))
                    .build();

            // When & Then
            naverApiClient
                    .post(DATALAB_SEARCH_PATH, request)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(500)));
        }
    }
}
