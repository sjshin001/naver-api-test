package com.naver.openapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DataLab 검색어 트렌드 API 응답
 * API 문서: https://developers.naver.com/docs/serviceapi/datalab/search/search.md
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchTrendResponse {
    
    /**
     * 조회 시작 일자
     */
    @JsonProperty("startDate")
    private String startDate;
    
    /**
     * 조회 종료 일자
     */
    @JsonProperty("endDate")
    private String endDate;
    
    /**
     * 구간 단위 (date/week/month)
     */
    @JsonProperty("timeUnit")
    private String timeUnit;
    
    /**
     * 검색어 그룹별 결과 목록
     */
    @JsonProperty("results")
    private List<TrendResult> results;
    
    /**
     * 검색어 그룹별 트렌드 결과
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendResult {
        
        /**
         * 그룹 이름
         */
        @JsonProperty("title")
        private String title;
        
        /**
         * 검색어 목록
         */
        @JsonProperty("keywords")
        private List<String> keywords;
        
        /**
         * 기간별 검색 비율 데이터
         */
        @JsonProperty("data")
        private List<TrendData> data;
    }
    
    /**
     * 기간별 검색 비율 데이터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        
        /**
         * 기간 (yyyy-MM-dd 형식)
         */
        @JsonProperty("period")
        private String period;
        
        /**
         * 검색 비율 (0~100)
         * 해당 기간 동안의 검색량을 0~100 사이의 값으로 정규화한 값
         */
        @JsonProperty("ratio")
        private Double ratio;
    }
}
