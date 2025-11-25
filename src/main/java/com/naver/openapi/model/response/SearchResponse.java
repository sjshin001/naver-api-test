package com.naver.openapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 검색 API 응답 데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    /**
     * 검색 결과를 생성한 시간
     */
    @JsonProperty("lastBuildDate")
    private String lastBuildDate;
    
    /**
     * 총 검색 결과 개수
     */
    @JsonProperty("total")
    private Integer total;
    
    /**
     * 검색 시작 위치
     */
    @JsonProperty("start")
    private Integer start;
    
    /**
     * 한 번에 표시할 검색 결과 개수
     */
    @JsonProperty("display")
    private Integer display;
    
    /**
     * 검색 결과 목록
     */
    @JsonProperty("items")
    private List<SearchItem> items;
    
    /**
     * 검색 결과 아이템
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchItem {
        
        /**
         * 검색 결과 문서의 제목
         */
        @JsonProperty("title")
        private String title;
        
        /**
         * 검색 결과 문서의 URL
         */
        @JsonProperty("link")
        private String link;
        
        /**
         * 검색 결과 문서의 내용 요약
         */
        @JsonProperty("description")
        private String description;
        
        /**
         * 검색 결과 문서가 네이버에 제공된 시간
         */
        @JsonProperty("pubDate")
        private String pubDate;
    }
}
