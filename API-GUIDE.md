# 📚 Naver Open API 공식 문서 가이드

이 문서는 실제 API 테스트 결과를 바탕으로 작성되었습니다.

---

## 📊 1. DataLab API - 검색어 트렌드

### **API 정보**
- **엔드포인트**: `POST /v1/datalab/search`
- **공식 문서**: https://developers.naver.com/docs/serviceapi/datalab/search/search.md

### **요청 파라미터**

#### **필수 파라미터**

| 파라미터 | 타입 | 설명 | 예시 |
|---------|------|------|------|
| `startDate` | String | 조회 시작 일자 (yyyy-MM-dd) | "2024-01-01" |
| `endDate` | String | 조회 종료 일자 (yyyy-MM-dd) | "2024-12-31" |
| `timeUnit` | String | 구간 단위: "date", "week", "month" | "month" |
| `keywordGroups` | Array | 검색어 그룹 목록 (최대 5개) | 아래 참조 |

#### **선택 파라미터** ⚠️ 중요!

| 파라미터 | 타입 | 설명 | 주의사항 |
|---------|------|------|---------|
| `device` | String | 기기: null(전체), "pc", "mo" | **null 전송 금지!** |
| `gender` | String | 성별: null(전체), "f", "m" | **null 전송 금지!** |
| `ages` | Array | 연령: ["1"~"11"] | **null 전송 금지!** |

### **⚠️ 중요: Null 처리**

```java
// ❌ 잘못된 예 - 400 에러 발생!
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [...],
  "device": null,      // ← 에러!
  "gender": null,      // ← 에러!
  "ages": null         // ← 에러!
}

// 에러 메시지:
// "TypeError: .device -> should be string"
```

```java
// ✅ 올바른 예 - null 필드는 아예 제외!
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [...]
  // device, gender, ages 필드 없음
}
```

### **해결책: @JsonInclude 사용**

```java
@JsonInclude(JsonInclude.Include.NON_NULL)  // ← 이 어노테이션 추가!
public class SearchTrendRequest {
    private String startDate;
    private String endDate;
    private String timeUnit;
    private List<KeywordGroup> keywordGroups;
    
    // 선택 필드 - null이면 JSON에서 제외됨
    private String device;
    private String gender;
    private List<String> ages;
}
```

### **KeywordGroup 구조**

```java
{
  "groupName": "자바",           // 그룹 이름
  "keywords": ["자바", "java"]   // 검색어 목록 (최대 20개)
}
```

### **응답 구조**

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
        }
      ]
    }
  ]
}
```

### **ratio 값의 의미**

- **범위**: 0.0 ~ 100.0
- **의미**: 검색량을 0~100 사이로 정규화한 상대적 수치
- **100**: 해당 기간 중 검색량이 가장 많았던 시점
- **0**: 검색량이 없거나 매우 적었던 시점

### **timeUnit 별 데이터 개수**

```java
// "date" (일간)
2024-01-01 ~ 2024-01-31 → 31개 데이터 포인트

// "week" (주간)
2024-01-01 ~ 2024-12-31 → 약 52개 데이터 포인트

// "month" (월간)
2024-01-01 ~ 2024-12-31 → 12개 데이터 포인트
```

---

## 🔍 2. Search API - 블로그 검색

### **API 정보**
- **엔드포인트**: `GET /v1/search/blog.json`
- **공식 문서**: https://developers.naver.com/docs/serviceapi/search/blog/blog.md

### **요청 파라미터**

| 파라미터 | 타입 | 필수 | 설명 | 기본값 |
|---------|------|------|------|--------|
| `query` | String | ✅ | 검색어 (UTF-8 인코딩) | - |
| `display` | Integer | ❌ | 검색 결과 개수 (1~100) | 10 |
| `start` | Integer | ❌ | 검색 시작 위치 (1~1000) | 1 |
| `sort` | String | ❌ | 정렬: "sim"(정확도), "date"(날짜) | "sim" |

### **응답 구조**

```json
{
  "lastBuildDate": "Tue, 25 Nov 2025 19:04:31 +0900",
  "total": 15486239,
  "start": 1,
  "display": 10,
  "items": [
    {
      "title": "강남 <b>맛집</b> 추천",
      "link": "https://blog.naver.com/...",
      "description": "강남역 근처 <b>맛집</b>을...",
      "bloggername": "홍길동",
      "bloggerlink": "https://blog.naver.com/...",
      "postdate": "20241125"
    }
  ]
}
```

### **페이징 처리**

```java
// 1페이지 (1~10번)
GET /v1/search/blog.json?query=맛집&display=10&start=1

// 2페이지 (11~20번)
GET /v1/search/blog.json?query=맛집&display=10&start=11

// 3페이지 (21~30번)
GET /v1/search/blog.json?query=맛집&display=10&start=21
```

---

## 📰 3. Search API - 뉴스 검색

### **API 정보**
- **엔드포인트**: `GET /v1/search/news.json`
- **공식 문서**: https://developers.naver.com/docs/serviceapi/search/news/news.md

### **블로그 검색과의 차이점**

```json
{
  "items": [
    {
      "title": "경제 뉴스 제목",
      "originallink": "https://원본기사.com",  // ← 뉴스만 있음
      "link": "https://news.naver.com/...",
      "description": "기사 내용...",
      "pubDate": "Tue, 25 Nov 2025 19:04:31 +0900"  // ← RFC 형식
    }
  ]
}
```

---

## 🖼️ 4. Search API - 이미지 검색

### **API 정보**
- **엔드포인트**: `GET /v1/search/image`
- **공식 문서**: https://developers.naver.com/docs/serviceapi/search/image/image.md

### **추가 파라미터**

| 파라미터 | 설명 | 값 |
|---------|------|-----|
| `filter` | 필터: "all", "large", "medium", "small" | "all" |
| `sort` | 정렬: "sim"(정확도), "date"(날짜) | "sim" |

### **응답 구조**

```json
{
  "items": [
    {
      "title": "고양이",
      "link": "https://...",
      "thumbnail": "https://...썸네일.jpg",
      "sizeheight": "333",
      "sizewidth": "500"
    }
  ]
}
```

---

## 🛒 5. Search API - 쇼핑 검색

### **API 정보**
- **엔드포인트**: `GET /v1/search/shop.json`
- **공식 문서**: https://developers.naver.com/docs/serviceapi/search/shopping/shopping.md

### **추가 파라미터**

| 파라미터 | 설명 | 값 |
|---------|------|-----|
| `sort` | 정렬 | "sim", "date", "asc"(낮은가격), "dsc"(높은가격) |

### **응답 구조**

```json
{
  "items": [
    {
      "title": "노트북",
      "link": "https://...",
      "image": "https://...이미지.jpg",
      "lprice": "890000",      // 최저가
      "hprice": "1200000",     // 최고가
      "mallName": "네이버쇼핑",
      "productId": "12345678",
      "productType": "1",      // 1:일반, 2:중고, 3:단종
      "brand": "삼성",
      "maker": "삼성전자",
      "category1": "디지털/가전",
      "category2": "노트북/PC",
      "category3": "노트북",
      "category4": "게이밍노트북"
    }
  ]
}
```

---

## 🔐 6. CAPTCHA API

### **API 정보**
- **공식 문서**: https://developers.naver.com/docs/serviceapi/captcha/captcha.md

### **사용 흐름**

```
1. 캡차 키 발급
   GET /v1/captcha/nkey?code=0
   → { "key": "BpFyDARp..." }

2. 캡차 이미지/음성 가져오기
   GET /v1/captcha/ncaptcha.bin?key={key}  (이미지)
   GET /v1/captcha/scaptcha?key={key}      (음성)

3. 사용자 입력 받기

4. 검증
   GET /v1/captcha/skey?code=1&key={key}&value={사용자입력}
   → { "result": true/false }
```

### **code 파라미터**

- `code=0`: 이미지 캡차
- `code=1`: 음성 캡차

---

## 👤 7. 네이버 로그인 API

### **API 정보**
- **공식 문서**: https://developers.naver.com/docs/login/api/api.md

### **OAuth 2.0 흐름**

```
1. 로그인 URL로 사용자 리디렉션
   https://nid.naver.com/oauth2.0/authorize?
     client_id={CLIENT_ID}&
     redirect_uri={CALLBACK_URL}&
     response_type=code&
     state={STATE}

2. 사용자 인증 후 Callback으로 Authorization Code 받기

3. Access Token 발급
   POST /oauth2.0/token
   → { "access_token": "...", "refresh_token": "..." }

4. 프로필 조회
   GET /v1/nid/me
   Authorization: Bearer {ACCESS_TOKEN}
```

### **프로필 응답**

```json
{
  "resultcode": "00",
  "message": "success",
  "response": {
    "id": "32742776",
    "nickname": "홍길동",
    "name": "홍길동",
    "email": "hong@naver.com",
    "gender": "M",
    "age": "40-49",
    "birthday": "05-16",
    "profile_image": "https://...",
    "birthyear": "1983",
    "mobile": "010-1234-5678"
  }
}
```

---

## 🗺️ 8. 지도 API - Geocoding

### **API 정보**
- **공식 문서**: https://developers.naver.com/docs/serviceapi/map/geocode/geocode.md

### **Geocoding (주소 → 좌표)**

```
GET /v1/map/geocode?query=강남역

Response:
{
  "addresses": [
    {
      "roadAddress": "서울특별시 강남구 테헤란로 152",
      "jibunAddress": "서울특별시 강남구 역삼동 737",
      "x": "127.027610",
      "y": "37.497942"
    }
  ]
}
```

### **Reverse Geocoding (좌표 → 주소)**

```
GET /v1/map/reversegeocode?coords=127.027610,37.497942&output=json

Response:
{
  "results": [
    {
      "name": "roadaddr",
      "region": {
        "area1": { "name": "서울특별시" },
        "area2": { "name": "강남구" }
      },
      "land": {
        "name": "역삼동",
        "number1": "737"
      }
    }
  ]
}
```

---

## 🔑 인증 헤더

모든 API에서 **공통으로 필요**:

```
X-Naver-Client-Id: {YOUR_CLIENT_ID}
X-Naver-Client-Secret: {YOUR_CLIENT_SECRET}
```

**예외**: 네이버 로그인 프로필 조회
```
Authorization: Bearer {ACCESS_TOKEN}
```

---

## ⚠️ 주요 에러 코드

| 상태 코드 | 에러 코드 | 의미 | 해결 방법 |
|----------|----------|------|----------|
| 401 | 024 | 인증 실패 | Client ID/Secret 확인 |
| 400 | 084 | 잘못된 요청 | 파라미터 형식 확인 |
| 429 | - | Rate Limit 초과 | 잠시 대기 후 재시도 |
| 500 | - | 서버 오류 | 잠시 후 재시도 |

---

## 🚀 Rate Limit

응답 헤더에서 확인 가능:

```
x-rate-limit: 10                    // 초당 10회
x-rate-limit-remaining: 6           // 남은 횟수
x-rate-limit-reset: 1764057519000   // 리셋 시간 (Unix timestamp)
```

---

## 📝 참고 사항

### **1. 날짜 형식**
- DataLab: `"2024-01-01"` (yyyy-MM-dd)
- 블로그 postdate: `"20241125"` (yyyyMMdd)
- 뉴스 pubDate: `"Tue, 25 Nov 2025 19:04:31 +0900"` (RFC)

### **2. HTML 태그 처리**
- 검색 결과의 `title`, `description`에는 `<b>검색어</b>` 형태로 강조 표시
- 실제 사용 시 HTML 태그 제거 필요

### **3. UTF-8 인코딩**
- 모든 한글 검색어는 UTF-8 인코딩 필수
- REST Assured는 자동 인코딩 처리

---

이 가이드는 실제 API 테스트 결과를 바탕으로 작성되었습니다. 🎯
