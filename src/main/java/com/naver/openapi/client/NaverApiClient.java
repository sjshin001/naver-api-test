package com.naver.openapi.client;

import com.naver.openapi.config.ApiConfig;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Naver Open API 전용 클라이언트
 * Client ID/Secret 헤더 자동 추가
 */
@Slf4j
public class NaverApiClient {
    
    private static final String HEADER_CLIENT_ID = "X-Naver-Client-Id";
    private static final String HEADER_CLIENT_SECRET = "X-Naver-Client-Secret";
    
    private final RestApiClient restApiClient;
    private final ApiConfig apiConfig;
    
    public NaverApiClient(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.restApiClient = new RestApiClient(apiConfig);
        log.info("NaverApiClient initialized");
    }
    
    /**
     * Naver API 인증 헤더 생성
     */
    private Map<String, String> createAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_CLIENT_ID, apiConfig.getClientId());
        headers.put(HEADER_CLIENT_SECRET, apiConfig.getClientSecret());
        return headers;
    }
    
    /**
     * 커스텀 헤더와 인증 헤더 병합
     */
    private Map<String, String> mergeHeaders(Map<String, String> customHeaders) {
        Map<String, String> headers = createAuthHeaders();
        if (customHeaders != null) {
            headers.putAll(customHeaders);
        }
        return headers;
    }
    
    /**
     * GET 요청 (인증 헤더 자동 추가)
     */
    public Response get(String path) {
        return restApiClient.getWithHeaders(path, createAuthHeaders());
    }
    
    /**
     * GET 요청 with Query Parameters (인증 헤더 자동 추가)
     */
    public Response get(String path, Map<String, ?> queryParams) {
        return restApiClient.getWithHeaders(path, createAuthHeaders(), queryParams);
    }
    
    /**
     * GET 요청 with Custom Headers (인증 헤더 자동 추가)
     */
    public Response getWithHeaders(String path, Map<String, String> customHeaders) {
        return restApiClient.getWithHeaders(path, mergeHeaders(customHeaders));
    }
    
    /**
     * GET 요청 with Custom Headers and Query Parameters (인증 헤더 자동 추가)
     */
    public Response getWithHeaders(String path, 
                                    Map<String, String> customHeaders,
                                    Map<String, ?> queryParams) {
        return restApiClient.getWithHeaders(path, mergeHeaders(customHeaders), queryParams);
    }
    
    /**
     * POST 요청 (인증 헤더 자동 추가)
     */
    public Response post(String path, Object body) {
        return restApiClient.postWithHeaders(path, createAuthHeaders(), body);
    }
    
    /**
     * POST 요청 with Custom Headers (인증 헤더 자동 추가)
     */
    public Response postWithHeaders(String path, 
                                     Map<String, String> customHeaders,
                                     Object body) {
        return restApiClient.postWithHeaders(path, mergeHeaders(customHeaders), body);
    }
    
    public ApiConfig getApiConfig() {
        return apiConfig;
    }
    
    public RestApiClient getRestApiClient() {
        return restApiClient;
    }
}
