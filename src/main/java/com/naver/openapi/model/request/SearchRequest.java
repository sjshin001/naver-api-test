package com.naver.openapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 검색 API 요청 파라미터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    /**
     * 검색어 (필수)
     */
    private String query;
    
    /**
     * 검색 결과 출력 건수 (기본값: 10, 최대: 100)
     */
    @Builder.Default
    private Integer display = 10;
    
    /**
     * 검색 시작 위치 (기본값: 1, 최대: 1000)
     */
    @Builder.Default
    private Integer start = 1;
    
    /**
     * 검색 결과 정렬 방식
     * - sim: 정확도순 (기본값)
     * - date: 날짜순
     */
    @Builder.Default
    private String sort = "sim";
    
    /**
     * 검색 필터
     * - all: 모든 문서 검색 (기본값)
     * - webkr: 한국어 웹문서만 검색
     */
    private String filter;
}
