package com.naver.openapi.config;

/**
 * 테스트 환경 정의
 */
public enum Environment {
    ALPHA("alpha", "Alpha 개발 환경"),
    BETA("beta", "Beta 검증 환경"),
    REAL("real", "Real 운영 환경");

    private final String name;
    private final String description;

    Environment(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Environment fromString(String env) {
        if (env == null || env.isBlank()) {
            return ALPHA; // 기본값
        }
        
        for (Environment environment : values()) {
            if (environment.name.equalsIgnoreCase(env.trim())) {
                return environment;
            }
        }
        
        throw new IllegalArgumentException(
            "Invalid environment: " + env + ". Valid values are: alpha, beta, real"
        );
    }
}
