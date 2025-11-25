package com.naver.openapi.config;

import com.naver.openapi.utils.PropertyLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * API 설정 관리 클래스
 */
@Slf4j
@Getter
public class ApiConfig {
    
    private final Environment environment;
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;
    private final int connectTimeout;
    private final int readTimeout;
    private final boolean loggingEnabled;
    
    private ApiConfig(Builder builder) {
        this.environment = builder.environment;
        this.baseUrl = builder.baseUrl;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.loggingEnabled = builder.loggingEnabled;
        
        log.info("API Config initialized - Environment: {}, BaseURL: {}", 
                 environment.getName(), baseUrl);
    }
    
    /**
     * 환경별 설정 로드
     */
    public static ApiConfig fromEnvironment(Environment environment, boolean isTest) {
        Properties properties = PropertyLoader.load(environment.getName(), isTest);
        
        return new Builder()
            .environment(environment)
            .baseUrl(PropertyLoader.getRequiredProperty(properties, "naver.api.baseUrl"))
            .clientId(PropertyLoader.getRequiredProperty(properties, "naver.clientId"))
            .clientSecret(PropertyLoader.getRequiredProperty(properties, "naver.clientSecret"))
            .connectTimeout(Integer.parseInt(
                PropertyLoader.getProperty(properties, "naver.api.connectTimeout", "5000")))
            .readTimeout(Integer.parseInt(
                PropertyLoader.getProperty(properties, "naver.api.readTimeout", "10000")))
            .loggingEnabled(Boolean.parseBoolean(
                PropertyLoader.getProperty(properties, "naver.api.logging.enabled", "true")))
            .build();
    }
    
    /**
     * 시스템 프로퍼티에서 환경 정보 가져오기
     */
    public static ApiConfig fromSystemProperty(boolean isTest) {
        String envProperty = System.getProperty("env", "alpha");
        Environment environment = Environment.fromString(envProperty);
        return fromEnvironment(environment, isTest);
    }
    
    public static class Builder {
        private Environment environment = Environment.ALPHA;
        private String baseUrl;
        private String clientId;
        private String clientSecret;
        private int connectTimeout = 5000;
        private int readTimeout = 10000;
        private boolean loggingEnabled = true;
        
        public Builder environment(Environment environment) {
            this.environment = environment;
            return this;
        }
        
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }
        
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        
        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        
        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
        
        public Builder loggingEnabled(boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
            return this;
        }
        
        public ApiConfig build() {
            if (baseUrl == null || baseUrl.isBlank()) {
                throw new IllegalStateException("baseUrl is required");
            }
            if (clientId == null || clientId.isBlank()) {
                throw new IllegalStateException("clientId is required");
            }
            if (clientSecret == null || clientSecret.isBlank()) {
                throw new IllegalStateException("clientSecret is required");
            }
            return new ApiConfig(this);
        }
    }
}
