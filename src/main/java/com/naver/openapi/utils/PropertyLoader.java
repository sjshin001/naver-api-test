package com.naver.openapi.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 환경별 설정 파일 로더
 */
@Slf4j
public class PropertyLoader {
    
    private static final String CONFIG_PATH = "config/%s.properties";
    private static final String TEST_CONFIG_PATH = "test-%s.properties";
    
    /**
     * 환경별 설정 파일 로드
     * 
     * @param environment 환경 (alpha, beta, real)
     * @param isTest 테스트 환경 여부
     * @return Properties 객체
     */
    public static Properties load(String environment, boolean isTest) {
        String configFile = isTest 
            ? String.format(TEST_CONFIG_PATH, environment)
            : String.format(CONFIG_PATH, environment);
        
        Properties properties = new Properties();
        
        try (InputStream input = PropertyLoader.class.getClassLoader()
                .getResourceAsStream(configFile)) {
            
            if (input == null) {
                log.error("Unable to find config file: {}", configFile);
                throw new RuntimeException("Configuration file not found: " + configFile);
            }
            
            properties.load(input);
            log.info("Successfully loaded configuration from: {}", configFile);
            
        } catch (IOException e) {
            log.error("Error loading configuration file: {}", configFile, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
        
        return properties;
    }
    
    /**
     * 메인 설정 파일 로드
     */
    public static Properties load(String environment) {
        return load(environment, false);
    }
    
    /**
     * 필수 속성 가져오기
     */
    public static String getRequiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required property not found: " + key);
        }
        return value;
    }
    
    /**
     * 선택적 속성 가져오기 (기본값 포함)
     */
    public static String getProperty(Properties properties, String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
