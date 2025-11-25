package com.naver.openapi.search;

import com.naver.openapi.base.TestBase;
import com.naver.openapi.model.request.SearchRequest;
import com.naver.openapi.model.response.SearchResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

/**
 * Naver 검색 API 테스트
 * 
 * API 문서: https://developers.naver.com/docs/serviceapi/search/blog/blog.md
 */
@Slf4j
@DisplayName("Naver Search API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchApiTest extends TestBase {
    
    private static final String SEARCH_BLOG_PATH = "/v1/search/blog.json";
    private static final String SEARCH_NEWS_PATH = "/v1/search/news.json";
    private static final String SEARCH_BOOK_PATH = "/v1/search/book.json";
    
    @Nested
    @DisplayName("블로그 검색 API 테스트")
    class BlogSearchTests {
        
        @Test
        @Order(1)
        @DisplayName("블로그 검색 - 기본 검색 성공")
        void testBlogSearch_Success() {
            // Given
            String query = "맛집";
            Map<String, Object> params = new HashMap<>();
            params.put("query", query);
            params.put("display", 10);
            params.put("start", 1);
            params.put("sort", "sim");
            
            log.info("Testing blog search with query: {}", query);
            
            // When
            Response response = naverApiClient
                .get(SEARCH_BLOG_PATH, params)
                .then()
                .log().all()
                .extract()
                .response();
            
            // Then - REST Assured Assertions
            response.then()
                .statusCode(200)
                .contentType("application/json;charset=UTF-8")
                .body("total", greaterThan(0))
                .body("items", notNullValue())
                .body("items.size()", greaterThan(0))
                .body("items[0].title", notNullValue())
                .body("items[0].link", notNullValue())
                .body("items[0].description", notNullValue());
            
            // Then - AssertJ Assertions
            SearchResponse searchResponse = response.as(SearchResponse.class);
            
            assertThat(searchResponse).isNotNull();
            assertThat(searchResponse.getTotal()).isGreaterThan(0);
            assertThat(searchResponse.getDisplay()).isEqualTo(10);
            assertThat(searchResponse.getStart()).isEqualTo(1);
            assertThat(searchResponse.getItems())
                .isNotEmpty()
                .hasSize(10)
                .allMatch(item -> item.getTitle() != null)
                .allMatch(item -> item.getLink() != null)
                .allMatch(item -> item.getDescription() != null);
            
            log.info("Successfully retrieved {} blog posts out of {} total", 
                     searchResponse.getItems().size(), 
                     searchResponse.getTotal());
            
            // 첫 번째 결과 로깅
            SearchResponse.SearchItem firstItem = searchResponse.getItems().get(0);
            log.info("First Result - Title: {}", firstItem.getTitle());
            log.info("First Result - Link: {}", firstItem.getLink());
        }
        
        @Test
        @Order(2)
        @DisplayName("블로그 검색 - Builder 패턴 사용")
        void testBlogSearch_WithBuilder() {
            // Given
            SearchRequest searchRequest = SearchRequest.builder()
                .query("Java Spring Boot")
                .display(5)
                .start(1)
                .sort("date")
                .build();
            
            Map<String, Object> params = new HashMap<>();
            params.put("query", searchRequest.getQuery());
            params.put("display", searchRequest.getDisplay());
            params.put("start", searchRequest.getStart());
            params.put("sort", searchRequest.getSort());
            
            log.info("Testing blog search with builder: {}", searchRequest);
            
            // When & Then
            SearchResponse response = naverApiClient
                .get(SEARCH_BLOG_PATH, params)
                .then()
                .statusCode(200)
                .extract()
                .as(SearchResponse.class);
            
            // Assertions
            assertThat(response.getItems()).hasSize(5);
            assertThat(response.getDisplay()).isEqualTo(5);
            
            log.info("Retrieved {} results", response.getItems().size());
        }
        
        @Test
        @Order(3)
        @DisplayName("블로그 검색 - 페이징 테스트")
        void testBlogSearch_Pagination() {
            // Given
            String query = "여행";
            int display = 10;
            
            // When - 첫 페이지
            SearchResponse firstPage = naverApiClient
                .get(SEARCH_BLOG_PATH, Map.of(
                    "query", query,
                    "display", display,
                    "start", 1
                ))
                .then()
                .statusCode(200)
                .extract()
                .as(SearchResponse.class);
            
            // When - 두 번째 페이지
            waitForRateLimit(100); // Rate Limit 방지
            
            SearchResponse secondPage = naverApiClient
                .get(SEARCH_BLOG_PATH, Map.of(
                    "query", query,
                    "display", display,
                    "start", 11
                ))
                .then()
                .statusCode(200)
                .extract()
                .as(SearchResponse.class);
            
            // Then
            assertThat(firstPage.getItems()).hasSize(display);
            assertThat(secondPage.getItems()).hasSize(display);
            assertThat(firstPage.getItems().get(0).getLink())
                .isNotEqualTo(secondPage.getItems().get(0).getLink());
            
            log.info("First page start: {}, Second page start: {}", 
                     firstPage.getStart(), secondPage.getStart());
        }
        
        @Test
        @Order(4)
        @DisplayName("블로그 검색 - 정렬 옵션 테스트 (정확도순)")
        void testBlogSearch_SortBySimilarity() {
            // Given & When
            SearchResponse response = naverApiClient
                .get(SEARCH_BLOG_PATH, Map.of(
                    "query", "맛집 추천",
                    "display", 5,
                    "sort", "sim"
                ))
                .then()
                .statusCode(200)
                .extract()
                .as(SearchResponse.class);
            
            // Then
            assertThat(response.getItems()).isNotEmpty();
            log.info("Retrieved {} results sorted by similarity", response.getItems().size());
        }
        
        @Test
        @Order(5)
        @DisplayName("블로그 검색 - 정렬 옵션 테스트 (날짜순)")
        void testBlogSearch_SortByDate() {
            // Given & When
            SearchResponse response = naverApiClient
                .get(SEARCH_BLOG_PATH, Map.of(
                    "query", "맛집 추천",
                    "display", 5,
                    "sort", "date"
                ))
                .then()
                .statusCode(200)
                .extract()
                .as(SearchResponse.class);
            
            // Then
            assertThat(response.getItems()).isNotEmpty();
            log.info("Retrieved {} results sorted by date", response.getItems().size());
        }
    }
    
    @Nested
    @DisplayName("뉴스 검색 API 테스트")
    class NewsSearchTests {
        
        @Test
        @Order(10)
        @DisplayName("뉴스 검색 - 기본 검색 성공")
        void testNewsSearch_Success() {
            // Given
            String query = "경제";
            
            log.info("Testing news search with query: {}", query);
            
            // When
            SearchResponse response = naverApiClient
                .get(SEARCH_NEWS_PATH, Map.of(
                    "query", query,
                    "display", 10,
                    "start", 1,
                    "sort", "date"
                ))
                .then()
                .statusCode(200)
                .body("total", greaterThan(0))
                .body("items", notNullValue())
                .extract()
                .as(SearchResponse.class);
            
            // Then
            assertThat(response.getTotal()).isGreaterThan(0);
            assertThat(response.getItems()).isNotEmpty();
            
            log.info("Successfully retrieved {} news articles", response.getItems().size());
            
            // 첫 번째 뉴스 정보 출력
            if (!response.getItems().isEmpty()) {
                SearchResponse.SearchItem firstNews = response.getItems().get(0);
                log.info("Latest News - Title: {}", firstNews.getTitle());
                log.info("Latest News - PubDate: {}", firstNews.getPubDate());
            }
        }
    }
    
    @Nested
    @DisplayName("에러 케이스 테스트")
    class ErrorCaseTests {
        
        @Test
        @Order(20)
        @DisplayName("검색어 누락 시 400 에러")
        void testSearch_MissingQuery_Returns400() {
            // Given - 검색어 없음
            Map<String, Object> params = new HashMap<>();
            params.put("display", 10);
            
            // When & Then
            naverApiClient
                .get(SEARCH_BLOG_PATH, params)
                .then()
                .statusCode(400)
                .body("errorMessage", notNullValue());
            
            log.info("Correctly returned 400 error for missing query");
        }
        
        @Test
        @Order(21)
        @DisplayName("잘못된 display 값 (최대값 초과)")
        void testSearch_InvalidDisplay_ExceedsMax() {
            // Given - display 값이 최대값(100)을 초과
            Map<String, Object> params = new HashMap<>();
            params.put("query", "테스트");
            params.put("display", 101);
            
            // When & Then
            naverApiClient
                .get(SEARCH_BLOG_PATH, params)
                .then()
                .statusCode(400)
                .body("errorMessage", notNullValue());
            
            log.info("Correctly returned 400 error for invalid display value");
        }
        
        @Test
        @Order(22)
        @DisplayName("잘못된 start 값 (최대값 초과)")
        void testSearch_InvalidStart_ExceedsMax() {
            // Given - start 값이 최대값(1000)을 초과
            Map<String, Object> params = new HashMap<>();
            params.put("query", "테스트");
            params.put("display", 10);
            params.put("start", 1001);
            
            // When & Then
            naverApiClient
                .get(SEARCH_BLOG_PATH, params)
                .then()
                .statusCode(400)
                .body("errorMessage", notNullValue());
            
            log.info("Correctly returned 400 error for invalid start value");
        }
    }
}
