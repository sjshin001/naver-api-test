# 📚 Naver DataLab API - 공식 문서 기반 가이드

공식 문서: https://developers.naver.com/docs/serviceapi/datalab/search/search.md

---

## 📊 네이버 통합 검색어 트렌드 조회

### **API 개요**

그룹으로 묶은 검색어에 대한 네이버 통합검색에서 검색 추이 데이터를 JSON 형식으로 반환합니다.

### **기본 정보**

| 항목 | 내용 |
|------|------|
| **요청 URL** | https://openapi.naver.com/v1/datalab/search |
| **프로토콜** | HTTPS |
| **HTTP 메서드** | POST |
| **데이터 형식** | JSON |

---

## 📝 요청 파라미터

파라미터를 **JSON 형식**으로 전달합니다.

### **필수 파라미터** ✅

| 파라미터 | 타입 | 설명 | 예시 |
|---------|------|------|------|
| `startDate` | string | 조회 기간 시작 날짜 (yyyy-MM-dd 형식)<br/>**2016년 1월 1일부터 조회 가능** | "2024-01-01" |
| `endDate` | string | 조회 기간 종료 날짜 (yyyy-MM-dd 형식) | "2024-12-31" |
| `timeUnit` | string | 구간 단위<br/>• `date`: 일간<br/>• `week`: 주간<br/>• `month`: 월간 | "month" |
| `keywordGroups` | array(JSON) | 주제어와 검색어 묶음 쌍의 배열<br/>**최대 5개** | 아래 참조 |
| `keywordGroups.groupName` | string | 주제어 (검색어 묶음을 대표하는 이름) | "자바" |
| `keywordGroups.keywords` | array(string) | 주제어에 해당하는 검색어<br/>**최대 20개** | ["자바", "java"] |

### **선택 파라미터** ⚙️

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `device` | string | 범위 (검색 환경)<br/>• 설정 안 함: 모든 환경<br/>• `pc`: PC에서 검색 추이<br/>• `mo`: 모바일에서 검색 추이 |
| `gender` | string | 성별<br/>• 설정 안 함: 모든 성별<br/>• `m`: 남성<br/>• `f`: 여성 |
| `ages` | array(string) | 연령 (아래 표 참조)<br/>• 설정 안 함: 모든 연령 |

### **연령 코드**

| 코드 | 연령대 | 코드 | 연령대 |
|------|--------|------|--------|
| `"1"` | 0∼12세 | `"7"` | 40∼44세 |
| `"2"` | 13∼18세 | `"8"` | 45∼49세 |
| `"3"` | 19∼24세 | `"9"` | 50∼54세 |
| `"4"` | 25∼29세 | `"10"` | 55∼59세 |
| `"5"` | 30∼34세 | `"11"` | 60세 이상 |
| `"6"` | 35∼39세 | | |

---

## ⚠️ 중요: 선택 파라미터 처리

### **문제 상황**

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [...],
  "device": null,     // ❌ 400 에러 발생!
  "gender": null,     // ❌ 400 에러 발생!
  "ages": null        // ❌ 400 에러 발생!
}
```

**에러 메시지:**
```json
{
  "errMsg": "TypeError: .device -> should be string",
  "errId": "2025-11-25T19:04:31+09:00/..."
}
```

### **올바른 방법**

#### **방법 1: 필드 제외** ✅ (권장)

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [...]
  // device, gender, ages 필드 자체를 포함하지 않음
}
```

#### **방법 2: 값 지정** ✅

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [...],
  "device": "mo",           // 모바일만
  "gender": "f",            // 여성만
  "ages": ["4", "5", "6"]   // 25~39세
}
```

### **Java 코드에서의 처리**

```java
// ✅ 올바른 방법 - @JsonInclude 사용
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에서 제외
public class SearchTrendRequest {
    private String device;
    private String gender;
    private List<String> ages;
}

// 사용 예시 1: 모든 환경/성별/연령 (필드 설정 안 함)
SearchTrendRequest request = SearchTrendRequest.builder()
    .startDate("2024-01-01")
    .endDate("2024-12-31")
    .timeUnit("month")
    .keywordGroups(...)
    // device, gender, ages 설정하지 않음
    .build();

// 사용 예시 2: 특정 조건 지정
SearchTrendRequest request = SearchTrendRequest.builder()
    .startDate("2024-01-01")
    .endDate("2024-12-31")
    .timeUnit("month")
    .keywordGroups(...)
    .device("mo")              // 모바일만
    .gender("f")               // 여성만
    .ages(List.of("4", "5"))   // 25~34세
    .build();
```

---

## 📤 요청 예시

### **예시 1: 기본 요청 (단일 키워드)**

```bash
curl -X POST "https://openapi.naver.com/v1/datalab/search" \
  -H "X-Naver-Client-Id: {YOUR_CLIENT_ID}" \
  -H "X-Naver-Client-Secret: {YOUR_CLIENT_SECRET}" \
  -H "Content-Type: application/json" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "timeUnit": "month",
    "keywordGroups": [
      {
        "groupName": "자바",
        "keywords": ["자바", "java"]
      }
    ]
  }'
```

### **예시 2: 키워드 비교 (여러 그룹)**

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [
    {
      "groupName": "자바",
      "keywords": ["자바", "java"]
    },
    {
      "groupName": "파이썬",
      "keywords": ["파이썬", "python"]
    },
    {
      "groupName": "자바스크립트",
      "keywords": ["자바스크립트", "javascript", "js"]
    }
  ]
}
```

### **예시 3: 필터 적용 (모바일 + 여성 + 25~39세)**

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "device": "mo",
  "gender": "f",
  "ages": ["4", "5", "6"],
  "keywordGroups": [
    {
      "groupName": "화장품",
      "keywords": ["화장품", "cosmetics", "메이크업"]
    }
  ]
}
```

---

## 📥 응답 예시

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "results": [
    {
      "title": "자바",
      "keywords": ["자바", "java"],
      "data": [
        {
          "period": "2024-01-01",
          "ratio": 45.5
        },
        {
          "period": "2024-02-01",
          "ratio": 52.3
        },
        {
          "period": "2024-03-01",
          "ratio": 48.7
        }
        // ... 12개 데이터 (월간인 경우)
      ]
    }
  ]
}
```

---

## 📊 응답 필드 설명

| 필드 | 타입 | 설명 |
|------|------|------|
| `startDate` | string | 조회 시작 날짜 |
| `endDate` | string | 조회 종료 날짜 |
| `timeUnit` | string | 구간 단위 |
| `results` | array | 키워드 그룹별 결과 |
| `results[].title` | string | 그룹 이름 (groupName과 동일) |
| `results[].keywords` | array | 검색어 목록 |
| `results[].data` | array | 기간별 검색 비율 데이터 |
| `results[].data[].period` | string | 기간 (yyyy-MM-dd) |
| `results[].data[].ratio` | number | 검색 비율 (0~100) |

### **ratio 값의 의미**

- **범위**: 0.0 ~ 100.0
- **의미**: 검색량을 0~100 사이로 정규화한 상대적 수치
- **100**: 해당 기간 중 검색량이 가장 많았던 시점
- **0**: 검색량이 없거나 매우 적었던 시점

---

## 🎯 주요 제약사항

| 제약 | 내용 |
|------|------|
| **조회 가능 기간** | 2016년 1월 1일부터 |
| **최대 키워드 그룹** | 5개 |
| **그룹당 최대 검색어** | 20개 |
| **선택 필드** | null 전송 시 400 에러 |
| **timeUnit별 데이터 수** | date: 일수, week: 주수, month: 월수 |

---

## 💡 사용 팁

### **1. 검색어 선택**

```java
// ✅ 좋은 예
keywords: ["자바", "java", "JAVA", "자바 프로그래밍"]

// ❌ 나쁜 예
keywords: ["자바"]  // 너무 적음
```

### **2. 시간 단위 선택**

```java
// 단기 트렌드 (1~3개월)
timeUnit: "date"  // 일간

// 중기 트렌드 (3~12개월)
timeUnit: "week"  // 주간

// 장기 트렌드 (1년 이상)
timeUnit: "month"  // 월간
```

### **3. 연령대 그룹핑**

```java
// 10대
ages: ["2"]  // 13~18세

// 20대
ages: ["3", "4"]  // 19~29세

// 30대
ages: ["5", "6"]  // 30~39세

// 40대 이상
ages: ["7", "8", "9", "10", "11"]  // 40세 이상
```

---

## 🔍 실전 예시

### **예시 1: 프로그래밍 언어 인기도 비교**

```java
SearchTrendRequest request = SearchTrendRequest.builder()
    .startDate("2023-01-01")
    .endDate("2024-12-31")
    .timeUnit("month")
    .keywordGroups(List.of(
        KeywordGroup.builder()
            .groupName("자바")
            .keywords(List.of("자바", "java", "Java"))
            .build(),
        KeywordGroup.builder()
            .groupName("파이썬")
            .keywords(List.of("파이썬", "python", "Python"))
            .build(),
        KeywordGroup.builder()
            .groupName("자바스크립트")
            .keywords(List.of("자바스크립트", "javascript", "JS"))
            .build()
    ))
    .build();
```

### **예시 2: 세대별 트렌드 분석**

```java
// 20대 여성의 화장품 검색 트렌드
SearchTrendRequest request = SearchTrendRequest.builder()
    .startDate("2024-01-01")
    .endDate("2024-12-31")
    .timeUnit("month")
    .device("mo")              // 모바일
    .gender("f")               // 여성
    .ages(List.of("3", "4"))   // 19~29세
    .keywordGroups(List.of(
        KeywordGroup.builder()
            .groupName("화장품")
            .keywords(List.of("화장품", "cosmetics", "메이크업"))
            .build()
    ))
    .build();
```

### **예시 3: PC vs 모바일 비교**

```java
// PC 검색 트렌드
SearchTrendRequest pcRequest = SearchTrendRequest.builder()
    .startDate("2024-01-01")
    .endDate("2024-12-31")
    .timeUnit("month")
    .device("pc")
    .keywordGroups(...)
    .build();

// 모바일 검색 트렌드
SearchTrendRequest mobileRequest = SearchTrendRequest.builder()
    .startDate("2024-01-01")
    .endDate("2024-12-31")
    .timeUnit("month")
    .device("mo")
    .keywordGroups(...)
    .build();
```

---

## 📚 참고 자료

- **공식 문서**: https://developers.naver.com/docs/serviceapi/datalab/search/search.md
- **Naver Developers**: https://developers.naver.com/
- **API 발급**: https://developers.naver.com/apps/#/list

---

이 가이드는 Naver 공식 API 문서를 기반으로 작성되었습니다. 🎯
