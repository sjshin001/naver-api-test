# 📡 Naver Open API - HTTP Requests

VS Code REST Client를 사용한 Naver Open API 테스트 요청 모음입니다.

## 🚀 빠른 시작

### 1. VS Code 확장 설치

```
Extensions → "REST Client" 검색 → 설치
또는
https://marketplace.visualstudio.com/items?itemName=humao.rest-client
```

### 2. Client ID/Secret 설정

`http-client.private.env.json` 파일 수정:

```json
{
  "dev": {
    "clientId": "YOUR_REAL_CLIENT_ID",
    "clientSecret": "YOUR_REAL_CLIENT_SECRET"
  }
}
```

### 3. 테스트 실행

1. `quick-test.http` 파일 열기
2. 요청 위에 커서를 놓고 **"Send Request"** 클릭
3. 또는 단축키: `Ctrl+Alt+R` (Mac: `Cmd+Alt+R`)

---

## 📁 파일 구조

```
http-requests/
├── http-client.env.json              # 환경 변수 (템플릿)
├── http-client.private.env.json      # 실제 키 저장 (Git 제외)
├── quick-test.http                   # 빠른 테스트용 ⭐ 여기서 시작!
├── search-api.http                   # 검색 API 전체
├── datalab-api.http                  # 데이터랩 API
├── profile-api.http                  # 프로필 API (OAuth 필요)
├── captcha-api.http                  # 캡차 API
├── other-api.http                    # 기타 API (번역, 지도 등)
└── README.md                         # 이 파일
```

---

## 🎯 API 목록

### **search-api.http** - 검색 API
- ✅ 블로그 검색 (정확도순/날짜순)
- ✅ 뉴스 검색
- ✅ 책 검색
- ✅ 카페글 검색
- ✅ 지역 검색
- ✅ 영화 검색
- ✅ 쇼핑 검색
- ✅ 백과사전 검색
- ✅ 웹문서 검색
- ✅ 이미지 검색

### **datalab-api.http** - 데이터랩 API
- ✅ 검색어 트렌드 (일/주/월)
- ✅ 키워드 비교
- ✅ 디바이스별 분석 (PC/모바일)
- ✅ 성별/연령 필터

### **other-api.http** - 기타 유용한 API
- ✅ 파파고 번역
- ✅ 언어 감지
- ✅ 단축 URL
- ✅ 지도 (Geocoding)
- ✅ 음성 합성 (TTS)
- ✅ QR 코드 생성

### **profile-api.http** - 프로필 API
- ⚠️ OAuth 2.0 Access Token 필요
- 사용자 프로필 조회

### **captcha-api.http** - 캡차 API
- ✅ 캡차 키 발급
- ✅ 이미지/음성 캡차
- ✅ 캡차 검증

---

## 🔧 환경 설정

### **환경 변수 파일**

#### `http-client.env.json` (공개 - 템플릿)
```json
{
  "dev": {
    "baseUrl": "https://openapi.naver.com",
    "clientId": "YOUR_CLIENT_ID_HERE",
    "clientSecret": "YOUR_CLIENT_SECRET_HERE"
  }
}
```

#### `http-client.private.env.json` (비공개 - 실제 키)
```json
{
  "dev": {
    "clientId": "abc123def456",
    "clientSecret": "XYZ789abc"
  }
}
```

### **환경 선택**

VS Code 하단 상태바:
```
현재: dev | alpha | beta | real
```

클릭하여 환경 전환 가능!

---

## 💡 사용 방법

### **1. 기본 사용**

```http
### 블로그 검색
GET {{baseUrl}}/v1/search/blog.json?query=맛집
X-Naver-Client-Id: {{clientId}}
X-Naver-Client-Secret: {{clientSecret}}
```

- `###` : 요청 구분자
- `{{변수}}` : 환경 변수 자동 치환

### **2. POST 요청**

```http
### 데이터랩 검색어 트렌드
POST {{baseUrl}}/v1/datalab/search
Content-Type: application/json
X-Naver-Client-Id: {{clientId}}
X-Naver-Client-Secret: {{clientSecret}}

{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "timeUnit": "month",
  "keywordGroups": [...]
}
```

### **3. 변수 사용**

```http
@captchaKey = 응답에서_받은_키값

GET {{baseUrl}}/v1/captcha/ncaptcha.bin?key={{captchaKey}}
X-Naver-Client-Id: {{clientId}}
X-Naver-Client-Secret: {{clientSecret}}
```

---

## ⌨️ 단축키

| 기능 | Windows/Linux | Mac |
|------|---------------|-----|
| 요청 실행 | `Ctrl+Alt+R` | `Cmd+Alt+R` |
| 이전 요청 | `Ctrl+Alt+P` | `Cmd+Alt+P` |
| 요청 취소 | `Ctrl+Alt+K` | `Cmd+Alt+K` |
| 환경 전환 | 상태바 클릭 | 상태바 클릭 |

---

## 📊 응답 확인

### **성공 (200 OK)**
```json
{
  "lastBuildDate": "...",
  "total": 15486239,
  "start": 1,
  "display": 10,
  "items": [...]
}
```

### **실패 (401 Unauthorized)**
```json
{
  "errorMessage": "Authentication failed. (인증에 실패했습니다.)",
  "errorCode": "024"
}
```

→ `http-client.private.env.json`의 Client ID/Secret 확인!

---

## 🔐 보안

### **.gitignore 설정**

```gitignore
# API Keys
http-requests/http-client.private.env.json
**/*-secret.*
```

### **주의사항**

❌ **절대로 Git에 올리면 안되는 파일:**
- `http-client.private.env.json`

✅ **Git에 올려도 되는 파일:**
- `http-client.env.json` (템플릿)
- `*.http` (요청 파일)

---

## 🎓 학습 리소스

### **Naver Developers 문서**
- https://developers.naver.com/docs/common/openapiguide/

### **REST Client 문서**
- https://github.com/Huachao/vscode-restclient

### **API 발급**
1. https://developers.naver.com/ 로그인
2. Application → 애플리케이션 등록
3. 사용 API 선택
4. Client ID/Secret 발급

---

## ❓ 문제 해결

### **Q: 401 Unauthorized 에러**
A: Client ID/Secret을 확인하세요.
```
http-client.private.env.json의 clientId, clientSecret 확인
```

### **Q: 환경 변수가 치환되지 않음**
A: VS Code를 재시작하거나 환경을 다시 선택하세요.

### **Q: Send Request가 안 보임**
A: REST Client 확장이 설치되어 있는지 확인하세요.

### **Q: 429 Too Many Requests**
A: Rate Limit 초과입니다. 잠시 대기 후 재시도하세요.
```
x-rate-limit: 10          (초당 10회)
x-rate-limit-remaining: 0 (남은 횟수)
```

---

## 🎯 추천 테스트 순서

1. **`quick-test.http`** ← 여기서 시작!
   - Health Check
   - 기본 검색 API

2. **`search-api.http`**
   - 다양한 검색 API 테스트

3. **`datalab-api.http`**
   - 검색어 트렌드 분석

4. **`other-api.http`**
   - 번역, 지도 등 유틸리티 API

---

## 📞 지원

- Naver Developers: https://developers.naver.com/support
- 이슈 리포트: https://github.com/your-repo/issues

---

**Happy Testing! 🚀**
