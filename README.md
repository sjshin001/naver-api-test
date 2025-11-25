# Naver Open API 자동화 테스트 프로젝트

Java 21, JUnit 5, REST Assured를 사용한 Naver Open API 자동화 테스트 프레임워크입니다.

## 🚀 프로젝트 특징

- **최신 기술 스택**: Java 21 LTS, JUnit 5.11.3, REST Assured 5.5.0
- **환경별 설정**: Alpha, Beta, Real 환경 분리 관리
- **모듈화 설계**: RestApiClient, NaverApiClient 분리로 재사용성 향상
- **Lombok 활용**: 보일러플레이트 코드 최소화
- **AssertJ**: 유연하고 읽기 쉬운 테스트 검증
- **상세 로깅**: Logback 기반 로깅 시스템
- **Maven 빌드**: 단일 빌드 도구로 간소화

## 📁 프로젝트 구조

```
naver-api-test/
├── src/
│   ├── main/
│   │   ├── java/com/naver/openapi/
│   │   │   ├── client/              # API 클라이언트
│   │   │   │   ├── RestApiClient.java
│   │   │   │   └── NaverApiClient.java
│   │   │   ├── config/              # 설정 관리
│   │   │   │   ├── ApiConfig.java
│   │   │   │   └── Environment.java
│   │   │   ├── model/               # 데이터 모델
│   │   │   │   ├── request/
│   │   │   │   │   └── SearchTrendRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── SearchResponse.java
│   │   │   │       └── SearchTrendResponse.java
│   │   │   └── utils/               # 유틸리티
│   │   │       └── PropertyLoader.java
│   │   └── resources/
│   │       ├── config/              # 환경별 설정
│   │       │   ├── alpha.properties
│   │       │   ├── beta.properties
│   │       │   └── real.properties
│   │       └── logback.xml
│   └── test/
│       ├── java/com/naver/openapi/
│       │   ├── base/
│       │   │   └── TestBase.java    # 테스트 베이스 클래스
│       │   ├── search/
│       │   │   └── SearchApiTest.java
│       │   ├── datalab/
│       │   │   └── DatalabSearchTest.java
│       │   └── nid/
│       └── resources/
│           ├── test-alpha.properties
│           ├── test-beta.properties
│           └── test-real.properties
├── http-requests/                   # VS Code REST Client
│   ├── quick-test.http
│   ├── search-api.http
│   ├── datalab-api.http
│   └── ...
├── pom.xml                          # Maven 설정
├── API-GUIDE.md                     # API 가이드
└── DATALAB-API-GUIDE.md             # DataLab API 상세 가이드
```

## 🔄 Properties 설정 흐름

프로젝트는 Properties 파일 기반 설정 시스템을 사용합니다. Client ID/Secret이 HTTP 헤더로 전달되는 전체 흐름은 다음과 같습니다:

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Properties 설정 흐름                          │
└─────────────────────────────────────────────────────────────────────┘

1️⃣  Properties 파일
    📁 src/test/resources/test-alpha.properties
    ┌─────────────────────────────────────────┐
    │ naver.clientId=YOUR_CLIENT_ID           │  ← 여기에 실제 값 입력!
    │ naver.clientSecret=YOUR_CLIENT_SECRET   │
    └─────────────────────────────────────────┘
                      ↓

2️⃣  PropertyLoader.java (읽기)
    📁 src/main/java/.../utils/PropertyLoader.java
    ┌─────────────────────────────────────────┐
    │ Properties properties = new Properties();│
    │ properties.load(input);                  │  ← 파일에서 읽기
    │ return properties.getProperty(key);      │
    └─────────────────────────────────────────┘
                      ↓

3️⃣  ApiConfig.java (저장)
    📁 src/main/java/.../config/ApiConfig.java
    ┌─────────────────────────────────────────┐
    │ private final String clientId;           │
    │ .clientId(PropertyLoader                 │  ← Properties에서 로드
    │     .getRequiredProperty(                │
    │         properties, "naver.clientId"))   │
    └─────────────────────────────────────────┘
                      ↓

4️⃣  NaverApiClient.java (사용)
    📁 src/main/java/.../client/NaverApiClient.java
    ┌─────────────────────────────────────────┐
    │ private Map<String, String>              │
    │     createAuthHeaders() {                │
    │   headers.put("X-Naver-Client-Id",       │  ← HTTP 헤더 생성
    │       apiConfig.getClientId());          │
    │ }                                        │
    └─────────────────────────────────────────┘
                      ↓

5️⃣  HTTP 요청 헤더
    ┌─────────────────────────────────────────┐
    │ POST https://openapi.naver.com/...      │
    │ Headers:                                 │
    │   X-Naver-Client-Id: YOUR_CLIENT_ID     │  ← Naver API로 전송
    │   X-Naver-Client-Secret: YOUR_SECRET    │
    └─────────────────────────────────────────┘
```

### 📝 핵심 포인트

| 단계 | 파일 | 역할 | 핵심 메서드/라인 |
|------|------|------|-----------------|
| 1 | `test-alpha.properties` | **값 저장** | `naver.clientId=...` (8번 줄) |
| 2 | `PropertyLoader.java` | **파일 읽기** | `getRequiredProperty()` (62번 줄) |
| 3 | `ApiConfig.java` | **값 저장** | `clientId(...)` (46번 줄) |
| 4 | `NaverApiClient.java` | **헤더 생성** | `createAuthHeaders()` (34번 줄) |
| 5 | `HTTP Request` | **API 전송** | `X-Naver-Client-Id` 헤더 |

### 🎯 사용자가 할 일

```properties
✅ src/test/resources/test-alpha.properties 파일만 수정!
   naver.clientId=YOUR_ACTUAL_CLIENT_ID       ← 실제 값으로 변경
   naver.clientSecret=YOUR_ACTUAL_SECRET      ← 실제 값으로 변경

❌ 다른 Java 파일은 수정 불필요 (자동 처리됨)
```

### 🔍 동작 원리

1. **테스트 시작** → `TestBase.java`가 초기화
2. **Properties 로드** → `PropertyLoader`가 파일 읽기
3. **ApiConfig 생성** → Client ID/Secret 저장
4. **NaverApiClient 생성** → ApiConfig 전달
5. **API 호출** → 자동으로 인증 헤더 추가

## 🔧 사전 요구사항

- **Java**: JDK 21 LTS 이상
- **Maven**: 3.6.x 이상
- **Naver Developers**: Client ID/Secret 발급 필요
  - https://developers.naver.com/

## ⚙️ 설정 방법

### 1. Client ID/Secret 설정

테스트 환경 설정 파일에 본인의 Client ID/Secret을 입력합니다:

```properties
# src/test/resources/test-alpha.properties
naver.clientId=YOUR_CLIENT_ID
naver.clientSecret=YOUR_CLIENT_SECRET
```

### 2. 환경별 설정 파일

각 환경별로 다음 설정을 조정할 수 있습니다:

- `naver.api.baseUrl`: API Base URL
- `naver.api.connectTimeout`: 연결 타임아웃 (ms)
- `naver.api.readTimeout`: 읽기 타임아웃 (ms)
- `naver.api.logging.enabled`: 로깅 활성화 여부

## 🏃 실행 방법

### Maven 명령어

#### 1. 프로젝트 컴파일

```bash
mvn clean compile
```

#### 2. 모든 테스트 실행 (기본 환경: Alpha)

```bash
mvn test
```

#### 3. 환경별 테스트 실행

```bash
# Alpha 환경
mvn test -Palpha

# Beta 환경
mvn test -Pbeta

# Real 환경
mvn test -Preal
```

#### 4. 특정 테스트 클래스 실행

```bash
# Search API 테스트
mvn test -Dtest=SearchApiTest -Palpha

# DataLab API 테스트
mvn test -Dtest=DatalabSearchTest -Palpha
```

#### 5. 특정 테스트 메서드 실행

```bash
mvn test -Dtest=SearchApiTest#testBlogSearch_Success -Palpha
```

#### 6. 패키지 생성

```bash
mvn clean package
```

### IntelliJ IDEA에서 실행

1. 프로젝트를 IntelliJ에서 Open
2. Maven 자동 Import 대기
3. 테스트 클래스 열기 (예: `SearchApiTest.java`)
4. 테스트 메서드 왼쪽의 ▶️ 아이콘 클릭
5. 또는 `Ctrl+Shift+F10` (Mac: `Cmd+Shift+R`)

#### 환경 변수 설정 (IntelliJ)

```
Run > Edit Configurations > VM options:
-Dspring.profiles.active=alpha
```

### VS Code REST Client 사용

HTTP 요청 파일을 사용한 빠른 API 테스트:

```bash
1. VS Code 확장: REST Client 설치
2. http-requests/quick-test.http 파일 열기
3. 요청 위에 "Send Request" 클릭
4. 또는 Ctrl+Alt+R (Mac: Cmd+Alt+R)
```

## 📊 테스트 API 목록

### Search API
- 블로그 검색
- 뉴스 검색
- 책 검색
- 카페글 검색
- 지역 검색
- 영화 검색
- 쇼핑 검색
- 백과사전 검색
- 웹문서 검색
- 이미지 검색

### DataLab API
- 검색어 트렌드 조회
- 키워드 비교
- 디바이스별 트렌드 (PC/모바일)
- 성별/연령별 트렌드

### 기타 API
- CAPTCHA API
- 네이버 로그인 API
- 지도 API (Geocoding)
- 파파고 번역 API
- 단축 URL API

## 🛠️ 기술 스택

| 분류 | 기술 | 버전 |
|------|------|------|
| **Language** | Java | 21 LTS |
| **Build Tool** | Maven | 3.9.x |
| **Test Framework** | JUnit 5 | 5.11.3 |
| **HTTP Client** | REST Assured | 5.5.0 |
| **Assertion Library** | AssertJ | 3.27.0 |
| **Code Generation** | Lombok | 1.18.36 |
| **JSON Processing** | Jackson | 2.18.2 |
| **Logging** | SLF4J + Logback | 2.0.16 / 1.5.12 |

## 📚 주요 클래스 설명

### TestBase (src/test/java/com/naver/openapi/base/)
- 모든 테스트 클래스의 기반 클래스
- 환경 설정 로드 및 API 클라이언트 초기화
- 테스트 생명주기 로깅

### RestApiClient (src/main/java/com/naver/openapi/client/)
- 범용 REST API 클라이언트
- REST Assured 기반 HTTP 요청 처리
- 공통 헤더, 타임아웃, 로깅 설정

### NaverApiClient (src/main/java/com/naver/openapi/client/)
- Naver API 전용 클라이언트
- Client ID/Secret 자동 헤더 추가
- Naver API 엔드포인트 처리

### ApiConfig (src/main/java/com/naver/openapi/config/)
- 환경별 설정 관리
- Properties 파일 로드
- 타임아웃, 로깅 등 설정 제공

## 🔍 테스트 작성 예시

### 기본 GET 요청

```java
@Test
@DisplayName("블로그 검색 - 기본 검색")
void testBlogSearch() {
    SearchResponse response = naverApiClient
        .get("/v1/search/blog.json", Map.of("query", "맛집"))
        .then()
        .statusCode(200)
        .extract()
        .as(SearchResponse.class);
    
    assertThat(response.getTotal()).isGreaterThan(0);
    assertThat(response.getItems()).isNotEmpty();
}
```

### POST 요청 (DataLab)

```java
@Test
@DisplayName("검색어 트렌드 조회")
void testSearchTrend() {
    SearchTrendRequest request = SearchTrendRequest.builder()
        .startDate("2024-01-01")
        .endDate("2024-12-31")
        .timeUnit("month")
        .keywordGroups(List.of(
            SearchTrendRequest.KeywordGroup.builder()
                .groupName("자바")
                .keywords(List.of("자바", "java"))
                .build()
        ))
        .build();
    
    SearchTrendResponse response = naverApiClient
        .post("/v1/datalab/search", request)
        .then()
        .statusCode(200)
        .extract()
        .as(SearchTrendResponse.class);
    
    assertThat(response.getResults()).hasSize(1);
}
```

## 📝 참고 문서

- **API-GUIDE.md**: 전체 Naver API 가이드
- **DATALAB-API-GUIDE.md**: DataLab API 상세 가이드
- **http-requests/README.md**: VS Code REST Client 사용법
- **공식 문서**: https://developers.naver.com/docs/

## 🚨 문제 해결

### 401 Unauthorized 에러

```
원인: Client ID/Secret이 잘못되었거나 설정되지 않음
해결: test-alpha.properties에서 올바른 값 확인
```

### 400 Bad Request (DataLab API)

```
원인: 선택 필드(device, gender, ages)에 null 전송
해결: @JsonInclude(JsonInclude.Include.NON_NULL) 확인
      또는 필드를 설정하지 않음
```

### 429 Too Many Requests

```
원인: Rate Limit 초과
해결: 잠시 대기 후 재시도 (응답 헤더의 x-rate-limit-reset 확인)
```

### Compilation Error

```bash
# Maven 클린 빌드
mvn clean compile

# IntelliJ에서 재빌드
Build > Rebuild Project
```

## 📄 라이선스

이 프로젝트는 학습 및 테스트 목적으로 만들어졌습니다.

## 🤝 기여

이슈나 개선 사항이 있으면 언제든지 Issue를 생성해주세요!

## 📧 문의

Naver Developers: https://developers.naver.com/

---

**Happy Testing! 🎉**
