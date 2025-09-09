# 📱 도서 검색 웹 + 앱 서비스 아키텍처 설계서

## 🎯 서비스 목표

### 비즈니스 목표
- 사용자가 웹과 모바일 앱에서 동일한 도서 검색 경험 제공
- 다중 API(알라딘, 카카오, 네이버) 통합 검색 결과 제공
- 개인화된 도서 관리 기능 (즐겨찾기, 읽고싶은 책, 읽은 책)
- 소셜 기능을 통한 독서 커뮤니티 형성

### 기술적 목표
- 마이크로서비스 지향 아키텍처
- RESTful API 설계
- 크로스 플랫폼 개발 (Web + Mobile)
- 확장 가능한 시스템 구조

## 🏗 전체 시스템 아키텍처

### High-Level Architecture
```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   Web Client    │  │  Mobile App     │  │   Admin Panel   │
│   (React)       │  │ (React Native)  │  │   (React)       │
└─────────────────┘  └─────────────────┘  └─────────────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               │
         ┌─────────────────────────────────┐
         │         API Gateway             │
         │      (Nginx / Kong)             │
         └─────────────────────────────────┘
                               │
         ┌─────────────────────────────────┐
         │       Backend Services          │
         │     (Spring Boot)               │
         │                                 │
         │  ┌─────────┐  ┌─────────────┐   │
         │  │  Auth   │  │   Book      │   │
         │  │ Service │  │  Service    │   │
         │  └─────────┘  └─────────────┘   │
         │                                 │
         │  ┌─────────┐  ┌─────────────┐   │
         │  │  User   │  │ External    │   │
         │  │ Service │  │ API Service │   │
         │  └─────────┘  └─────────────┘   │
         └─────────────────────────────────┘
                               │
    ┌─────────────┬─────────────┼─────────────┬─────────────┐
    │             │             │             │             │
┌───────┐ ┌───────────┐ ┌─────────────┐ ┌──────────┐ ┌──────────┐
│PostgreSQL │   Redis   ││   File      ││ External ││ External │
│(Main DB)│ │  (Cache)  ││  Storage    ││   APIs   ││   APIs   │
└─────────┘ └───────────┘ └─────────────┘ └──────────┘ └──────────┘
                              │              (Aladin)   (Kakao)
                         ┌──────────┐                   (Naver)
                         │   S3 /   │
                         │ MinIO    │
                         └──────────┘
```

## 📱 클라이언트 아키텍처

### 웹 클라이언트 (React)
```
src/
├── components/           # 재사용 가능한 컴포넌트
│   ├── common/          # 공통 컴포넌트 (Header, Footer, Loading)
│   ├── book/            # 도서 관련 컴포넌트
│   └── user/            # 사용자 관련 컴포넌트
├── pages/               # 페이지 컴포넌트
│   ├── Home.tsx
│   ├── Search.tsx
│   ├── BookDetail.tsx
│   └── MyLibrary.tsx
├── hooks/               # 커스텀 훅
├── services/            # API 호출 로직
├── store/               # 상태 관리 (Redux Toolkit)
├── utils/               # 유틸리티 함수
└── types/               # TypeScript 타입 정의
```

### 모바일 앱 (React Native)
```
src/
├── components/          # 네이티브 컴포넌트
├── screens/             # 화면 컴포넌트
├── navigation/          # 내비게이션 설정
├── services/            # API 서비스 (웹과 공유)
├── store/               # 상태 관리 (웹과 공유)
├── hooks/               # 모바일 전용 훅
└── utils/               # 모바일 유틸리티
```

## 🚀 백엔드 서비스 아키텍처

### 메인 서비스 구조
```
src/main/java/com/books/
├── config/                 # 설정 클래스
│   ├── SecurityConfig.java
│   ├── WebClientConfig.java
│   └── DatabaseConfig.java
├── common/                 # 공통 모듈
│   ├── dto/               # 공통 DTO
│   ├── exception/         # 예외 처리
│   └── util/              # 유틸리티 클래스
├── auth/                   # 인증/인가 모듈
│   ├── api/
│   ├── application/
│   ├── domain/
│   └── infrastructure/
├── user/                   # 사용자 관리 모듈
│   ├── api/
│   ├── application/
│   ├── domain/
│   └── infrastructure/
├── book/                   # 도서 관리 모듈
│   ├── api/
│   ├── application/
│   ├── domain/
│   └── infrastructure/
└── external/               # 외부 API 연동 모듈
    ├── api/
    ├── application/
    └── infrastructure/
```

## 🗄 데이터베이스 설계

### 핵심 엔티티 관계도
```
Users ──┐
        │
        ├── UserBooks (Many-to-Many with status)
        │
        └── SearchLogs
        
Books ──┐
        │
        ├── BookReviews
        │
        └── BookCategories

Categories ──── BookCategories
```

### 상세 테이블 설계

#### 사용자 관련 테이블
```sql
-- 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500),
    provider VARCHAR(20) DEFAULT 'LOCAL', -- LOCAL, GOOGLE, KAKAO
    provider_id VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 사용자 프로필 테이블
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    bio TEXT,
    favorite_genres VARCHAR(500),
    reading_goal INTEGER DEFAULT 0,
    is_public BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

#### 도서 관련 테이블
```sql
-- 도서 테이블
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500),
    author VARCHAR(300),
    publisher VARCHAR(200),
    published_date DATE,
    page_count INTEGER,
    description TEXT,
    thumbnail_url VARCHAR(500),
    price INTEGER,
    currency VARCHAR(10) DEFAULT 'KRW',
    category_id BIGINT REFERENCES categories(id),
    external_ids JSON, -- {aladin_id, kakao_id, naver_id}
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 카테고리 테이블
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- 사용자-도서 관계 테이블
CREATE TABLE user_books (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    book_id BIGINT REFERENCES books(id) ON DELETE CASCADE,
    status VARCHAR(20) CHECK (status IN ('FAVORITE', 'WISHLIST', 'READING', 'COMPLETED')),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    reading_started_at TIMESTAMP,
    reading_completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, book_id, status)
);
```

#### 검색 및 로그 테이블
```sql
-- 검색 로그 테이블
CREATE TABLE search_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    query VARCHAR(300) NOT NULL,
    api_sources VARCHAR(100), -- comma-separated: aladin,kakao,naver
    results_count INTEGER DEFAULT 0,
    response_time_ms INTEGER,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 인기 검색어 테이블
CREATE TABLE popular_searches (
    id BIGSERIAL PRIMARY KEY,
    query VARCHAR(300) UNIQUE NOT NULL,
    search_count INTEGER DEFAULT 1,
    last_searched_at TIMESTAMP DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW()
);
```

## 🔧 기술 스택 상세

### 프론트엔드 기술 스택

#### 웹 (React)
```json
{
  "framework": "React 18",
  "language": "TypeScript",
  "ui_library": "Material-UI (MUI) v5",
  "routing": "React Router v6",
  "state_management": "Redux Toolkit + RTK Query",
  "styling": "Emotion + MUI System",
  "bundler": "Vite",
  "testing": "Jest + React Testing Library"
}
```

#### 모바일 (React Native)
```json
{
  "framework": "React Native 0.72",
  "navigation": "React Navigation v6",
  "ui_library": "React Native Elements + Native Base",
  "state_management": "Redux Toolkit (웹과 공유)",
  "icons": "React Native Vector Icons",
  "images": "React Native Fast Image",
  "storage": "AsyncStorage"
}
```

### 백엔드 기술 스택

#### Core Framework (유지)
```yaml
Spring Boot: 3.4.4
Java: 21
Kotlin: 1.9.22 (선택적 사용)
```

#### 추가 기술 스택
```yaml
Security: Spring Security + JWT
Database: PostgreSQL 15+
Cache: Redis 7+
Message Queue: RabbitMQ (선택사항)
File Storage: MinIO or AWS S3
Monitoring: Micrometer + Prometheus
Documentation: SpringDoc OpenAPI 3
```

## 🔐 보안 아키텍처

### 인증/인가 전략
1. **JWT 기반 토큰 인증**
   - Access Token (15분)
   - Refresh Token (7일)
   - Redis에 토큰 블랙리스트 관리

2. **소셜 로그인 지원**
   - Google OAuth2
   - Kakao OAuth2
   - 기본 이메일 로그인

3. **API 보안**
   - Rate Limiting (Redis)
   - CORS 설정
   - API Key 환경변수 관리

## 🚀 배포 아키텍처

### 개발/스테이징/운영 환경
```yaml
Development:
  - Local Docker Compose
  - H2 → PostgreSQL
  - 로컬 Redis

Staging:
  - Docker + Docker Compose
  - PostgreSQL (RDS)
  - Redis (ElastiCache)

Production:
  - Kubernetes or Docker Swarm
  - Load Balancer (ALB)
  - CDN (CloudFront)
  - Monitoring (Grafana + Prometheus)
```

## 📊 성능 고려사항

### 캐싱 전략
1. **Redis 캐싱**
   - 외부 API 응답 (1시간)
   - 인기 검색 결과 (30분)
   - 사용자 세션 정보

2. **DB 최적화**
   - 검색 쿼리 인덱스
   - 페이지네이션
   - 연관 관계 지연 로딩

### API 성능
- 외부 API 병렬 호출
- 응답 시간 모니터링
- 실패 재시도 로직

## 🔄 확장 계획

### 단계별 확장 로드맵
1. **Phase 1**: 기본 웹/앱 서비스
2. **Phase 2**: 소셜 기능 (팔로우, 리뷰)
3. **Phase 3**: AI 추천 시스템
4. **Phase 4**: 독서 커뮤니티 기능
5. **Phase 5**: 전자책 리더 통합

이 아키텍처는 현재 프로젝트를 기반으로 확장 가능한 웹 + 앱 서비스를 구축하기 위한 포괄적인 설계입니다.