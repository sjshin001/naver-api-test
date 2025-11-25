package com.naver.openapi.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 네이버 통합 검색어 트렌드 조회 요청
 * 
 * 그룹으로 묶은 검색어에 대한 네이버 통합검색에서 검색 추이 데이터를 조회합니다.
 * 
 * <p><b>API 정보</b></p>
 * <ul>
 *   <li>엔드포인트: POST https://openapi.naver.com/v1/datalab/search</li>
 *   <li>공식 문서: https://developers.naver.com/docs/serviceapi/datalab/search/search.md</li>
 * </ul>
 * 
 * <p><b>주요 제약사항</b></p>
 * <ul>
 *   <li>조회 가능 기간: 2016년 1월 1일부터</li>
 *   <li>최대 키워드 그룹 수: 5개</li>
 *   <li>그룹당 최대 검색어 수: 20개</li>
 *   <li>선택 필드(device, gender, ages): null 전송 금지 ⚠️</li>
 * </ul>
 * 
 * <p><b>사용 예시</b></p>
 * <pre>{@code
 * SearchTrendRequest request = SearchTrendRequest.builder()
 *     .startDate("2024-01-01")
 *     .endDate("2024-12-31")
 *     .timeUnit("month")
 *     .keywordGroups(List.of(
 *         SearchTrendRequest.KeywordGroup.builder()
 *             .groupName("자바")
 *             .keywords(List.of("자바", "java"))
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 * 
 * @see SearchTrendResponse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에서 제외
public class SearchTrendRequest {
    
    /**
     * 조회 기간 시작 날짜
     * 
     * <p><b>필수 여부:</b> Y (필수)</p>
     * <p><b>형식:</b> yyyy-MM-dd</p>
     * <p><b>제약:</b> 2016년 1월 1일부터 조회 가능</p>
     * 
     * <p><b>예시:</b> "2024-01-01"</p>
     */
    @JsonProperty("startDate")
    private String startDate;
    
    /**
     * 조회 기간 종료 날짜
     * 
     * <p><b>필수 여부:</b> Y (필수)</p>
     * <p><b>형식:</b> yyyy-MM-dd</p>
     * 
     * <p><b>예시:</b> "2024-12-31"</p>
     */
    @JsonProperty("endDate")
    private String endDate;
    
    /**
     * 구간 단위
     * 
     * <p><b>필수 여부:</b> Y (필수)</p>
     * 
     * <p><b>가능한 값:</b></p>
     * <ul>
     *   <li>"date": 일간</li>
     *   <li>"week": 주간</li>
     *   <li>"month": 월간</li>
     * </ul>
     * 
     * <p><b>예시:</b> "month"</p>
     */
    @JsonProperty("timeUnit")
    private String timeUnit;
    
    /**
     * 주제어와 검색어 묶음 쌍의 배열
     * 
     * <p><b>필수 여부:</b> Y (필수)</p>
     * <p><b>제약:</b> 최대 5개의 그룹</p>
     * 
     * <p><b>예시:</b></p>
     * <pre>{@code
     * List.of(
     *     KeywordGroup.builder()
     *         .groupName("자바")
     *         .keywords(List.of("자바", "java"))
     *         .build(),
     *     KeywordGroup.builder()
     *         .groupName("파이썬")
     *         .keywords(List.of("파이썬", "python"))
     *         .build()
     * )
     * }</pre>
     */
    @JsonProperty("keywordGroups")
    private List<KeywordGroup> keywordGroups;
    
    /**
     * 범위 (검색 환경)
     * 
     * <p><b>필수 여부:</b> N (선택)</p>
     * 
     * <p><b>가능한 값:</b></p>
     * <ul>
     *   <li>설정 안 함 (null 또는 필드 제외): 모든 환경</li>
     *   <li>"pc": PC에서 검색 추이</li>
     *   <li>"mo": 모바일에서 검색 추이</li>
     * </ul>
     * 
     * <p><b>⚠️ 중요:</b> null을 JSON에 포함하여 전송하면 400 에러 발생!</p>
     * <ul>
     *   <li>모든 환경: 이 필드를 설정하지 않음 (Builder에서 device() 호출 안 함)</li>
     *   <li>PC만: .device("pc")</li>
     *   <li>모바일만: .device("mo")</li>
     * </ul>
     */
    @JsonProperty("device")
    private String device;
    
    /**
     * 성별
     * 
     * <p><b>필수 여부:</b> N (선택)</p>
     * 
     * <p><b>가능한 값:</b></p>
     * <ul>
     *   <li>설정 안 함 (null 또는 필드 제외): 모든 성별</li>
     *   <li>"m": 남성</li>
     *   <li>"f": 여성</li>
     * </ul>
     * 
     * <p><b>⚠️ 중요:</b> null을 JSON에 포함하여 전송하면 400 에러 발생!</p>
     * <ul>
     *   <li>모든 성별: 이 필드를 설정하지 않음</li>
     *   <li>남성만: .gender("m")</li>
     *   <li>여성만: .gender("f")</li>
     * </ul>
     */
    @JsonProperty("gender")
    private String gender;
    
    /**
     * 연령
     * 
     * <p><b>필수 여부:</b> N (선택)</p>
     * 
     * <p><b>가능한 값:</b></p>
     * <ul>
     *   <li>설정 안 함 (null 또는 필드 제외): 모든 연령</li>
     *   <li>"1": 0∼12세</li>
     *   <li>"2": 13∼18세</li>
     *   <li>"3": 19∼24세</li>
     *   <li>"4": 25∼29세</li>
     *   <li>"5": 30∼34세</li>
     *   <li>"6": 35∼39세</li>
     *   <li>"7": 40∼44세</li>
     *   <li>"8": 45∼49세</li>
     *   <li>"9": 50∼54세</li>
     *   <li>"10": 55∼59세</li>
     *   <li>"11": 60세 이상</li>
     * </ul>
     * 
     * <p><b>예시:</b></p>
     * <ul>
     *   <li>20대: List.of("3", "4")</li>
     *   <li>25~39세: List.of("4", "5", "6")</li>
     *   <li>40대 이상: List.of("7", "8", "9", "10", "11")</li>
     * </ul>
     * 
     * <p><b>⚠️ 중요:</b> null을 JSON에 포함하여 전송하면 400 에러 발생!</p>
     */
    @JsonProperty("ages")
    private List<String> ages;
    
    /**
     * 검색어 그룹
     * 
     * 주제어와 해당 주제어를 대표하는 검색어 목록의 쌍
     * 
     * <p><b>사용 예시:</b></p>
     * <pre>{@code
     * KeywordGroup.builder()
     *     .groupName("커피")
     *     .keywords(List.of("커피", "coffee", "카페"))
     *     .build()
     * }</pre>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordGroup {
        
        /**
         * 주제어 (그룹 이름)
         * 
         * <p><b>필수 여부:</b> Y (필수)</p>
         * 
         * <p>검색어 묶음을 대표하는 이름입니다.</p>
         * 
         * <p><b>예시:</b> "자바", "파이썬", "커피"</p>
         */
        @JsonProperty("groupName")
        private String groupName;
        
        /**
         * 주제어에 해당하는 검색어 목록
         * 
         * <p><b>필수 여부:</b> Y (필수)</p>
         * <p><b>제약:</b> 최대 20개의 검색어</p>
         * 
         * <p>주제어에 해당하는 동의어, 유사어, 관련어 등을 포함합니다.</p>
         * 
         * <p><b>예시:</b></p>
         * <ul>
         *   <li>List.of("자바", "java", "JAVA")</li>
         *   <li>List.of("커피", "coffee", "카페", "에스프레소")</li>
         * </ul>
         */
        @JsonProperty("keywords")
        private List<String> keywords;
    }
}
