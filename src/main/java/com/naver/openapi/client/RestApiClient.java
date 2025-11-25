package com.naver.openapi.client;

import com.naver.openapi.config.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * REST API 공통 클라이언트
 * RestAssured 기반의 HTTP 요청 처리
 */
@Slf4j
public class RestApiClient {
    
    private final ApiConfig apiConfig;
    private final RequestSpecification requestSpec;
    
    public RestApiClient(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.requestSpec = createRequestSpecification();
        log.info("RestApiClient initialized for environment: {}", 
                 apiConfig.getEnvironment().getName());
    }
    
    /**
     * RequestSpecification 생성
     */
    private RequestSpecification createRequestSpecification() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
            .setBaseUri(apiConfig.getBaseUrl())
            .setContentType(ContentType.JSON)
            .setConfig(createRestAssuredConfig());
        
        // 로깅 필터 추가
        if (apiConfig.isLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter())
                   .addFilter(new ResponseLoggingFilter());
        }
        
        return builder.build();
    }
    
    /**
     * RestAssured 설정 생성
     */
    private RestAssuredConfig createRestAssuredConfig() {
        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
            .setParam("http.connection.timeout", apiConfig.getConnectTimeout())
            .setParam("http.socket.timeout", apiConfig.getReadTimeout());
        
        LogConfig logConfig = LogConfig.logConfig()
            .enableLoggingOfRequestAndResponseIfValidationFails()
            .enablePrettyPrinting(true);
        
        return RestAssuredConfig.config()
            .httpClient(httpClientConfig)
            .logConfig(logConfig);
    }
    
    /**
     * GET 요청
     */
    public Response get(String path) {
        log.debug("GET request to: {}", path);
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .get(path);
    }
    
    /**
     * GET 요청 with Query Parameters
     */
    public Response get(String path, Map<String, ?> queryParams) {
        log.debug("GET request to: {} with params: {}", path, queryParams);
        return RestAssured.given()
            .spec(requestSpec)
            .queryParams(queryParams)
            .when()
            .get(path);
    }
    
    /**
     * POST 요청
     */
    public Response post(String path, Object body) {
        log.debug("POST request to: {}", path);
        return RestAssured.given()
            .spec(requestSpec)
            .body(body)
            .when()
            .post(path);
    }
    
    /**
     * PUT 요청
     */
    public Response put(String path, Object body) {
        log.debug("PUT request to: {}", path);
        return RestAssured.given()
            .spec(requestSpec)
            .body(body)
            .when()
            .put(path);
    }
    
    /**
     * DELETE 요청
     */
    public Response delete(String path) {
        log.debug("DELETE request to: {}", path);
        return RestAssured.given()
            .spec(requestSpec)
            .when()
            .delete(path);
    }
    
    /**
     * 커스텀 헤더와 함께 GET 요청
     */
    public Response getWithHeaders(String path, Map<String, String> headers) {
        log.debug("GET request to: {} with headers: {}", path, headers);
        return RestAssured.given()
            .spec(requestSpec)
            .headers(headers)
            .when()
            .get(path);
    }
    
    /**
     * 커스텀 헤더와 함께 GET 요청 with Query Parameters
     */
    public Response getWithHeaders(String path, 
                                    Map<String, String> headers, 
                                    Map<String, ?> queryParams) {
        log.debug("GET request to: {} with headers: {} and params: {}", 
                  path, headers, queryParams);
        return RestAssured.given()
            .spec(requestSpec)
            .headers(headers)
            .queryParams(queryParams)
            .when()
            .get(path);
    }
    
    /**
     * 커스텀 헤더와 함께 POST 요청
     */
    public Response postWithHeaders(String path, 
                                     Map<String, String> headers, 
                                     Object body) {
        log.debug("POST request to: {} with headers: {}", path, headers);
        return RestAssured.given()
            .spec(requestSpec)
            .headers(headers)
            .body(body)
            .when()
            .post(path);
    }
    
    public ApiConfig getApiConfig() {
        return apiConfig;
    }
}
