# 📚 Doky Book 프로젝트 개발 계획

## 🎯 프로젝트 분석 요약

### 프로젝트 개요
**Spring Boot 3.4.4** 기반의 도서 검색 통합 시스템으로, **Java 21**과 **Kotlin 1.9.22**로 동일한 기능을 병행 구현한 학습용 프로젝트입니다.

### 핵심 특징
- **다중 외부 API 통합**: 알라딘, 카카오, 네이버 도서 검색 API
- **이중 언어 구현**: Java와 Kotlin으로 동일 기능 구현
- **WebFlux 비동기 처리**: Reactive Programming 패턴 적용
- **H2 인메모리 DB**: 개발 및 테스트 환경 최적화
- **Swagger UI**: API 문서화 및 테스트 도구

## 🏗️ 아키텍처 분석

### 패키지 구조
```
src/
├── main/
│   ├── java/com/books/                    # Java 구현체
│   │   ├── config/WebClientConfig.java    # WebClient 설정
│   │   └── external/                      # 외부 API 연동
│   │       ├── api/                       # REST 컨트롤러 & DTO
│   │       └── application/               # 비즈니스 로직
│   │           ├── ExternalBooksFacade    # 3개 서비스 통합 파사드
│   │           ├── AladinBookService      # 알라딘 API 서비스
│   │           ├── KakaoBookService       # 카카오 API 서비스
│   │           └── NaverBookService       # 네이버 API 서비스
│   │
│   ├── kotlin/com/books/                  # Kotlin 구현체
│   │   ├── book/                          # 내부 도서 관리
│   │   └── external/                      # 외부 API 연동
│   │       ├── application/               
│   │       │   └── KotlinBooksFacade      # 카카오 서비스 래핑
│   │       └── api/                       # Kotlin 전용 DTO
│   │
│   └── resources/
│       └── application.yml                # 설정 파일 (API 키 포함)
│
└── test/
    └── java/com/books/
        ├── java/practice/                 # Java 연습 코드
        └── kotlin/practice/               # Kotlin 연습 코드
```

### 주요 아키텍처 패턴

#### 1. Facade 패턴
- **ExternalBooksFacade** (Java): 알라딘, 카카오, 네이버 3개 서비스 통합
- **KotlinBooksFacade** (Kotlin): 카카오 서비스만 래핑

#### 2. Service 계층 분리
각 외부 API별로 일관된 구조:
- `Service` 인터페이스
- `ServiceImpl` 구현체
- Request/Response DTO 분리

#### 3. 언어별 특성 활용
- **Java**: Lombok을 활용한 보일러플레이트 제거
- **Kotlin**: 데이터 클래스, 널 안전성 등 언어 특성 활용

## 🔧 개발 환경 설정

### 필수 요구사항
- **Java 21** (toolchain 설정됨)
- **Gradle 8.13**
- **IDE**: IntelliJ IDEA 권장 (Java + Kotlin 지원)

### 실행 명령어
```bash
# 애플리케이션 실행
./gradlew bootRun

# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.books.external.application.AladinBookServiceTest"
```

### 개발 도구 접근
- **H2 Console**: http://localhost:8080/h2-console
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **메인 애플리케이션**: http://localhost:8080

## 🚀 향후 개발 가이드라인

### 새로운 외부 API 추가 시
1. **Request/Response DTO 생성**
   - `src/main/java/com/books/external/api/payload/` 하위에 추가
   - API별 전용 패키지 생성

2. **Service 구현**
   - `Service` 인터페이스 정의
   - `ServiceImpl` 구현체 작성
   - WebClient를 활용한 비동기 호출

3. **Facade 통합**
   - `ExternalBooksFacade`에 새 서비스 추가
   - 메소드 오버로딩으로 다형성 활용

4. **설정 추가**
   - `application.yml`에 API 키 및 엔드포인트 설정
   - `WebClientConfig`에 필요한 설정 추가

5. **Kotlin 버전 고려**
   - 학습 목적으로 Kotlin 버전도 구현 검토
   - 언어별 특성을 살린 구현 방식 적용

### 테스트 작성 가이드
- **연습 코드**: `practice` 패키지에 학습용 테스트 작성
- **서비스 테스트**: WebClient mocking 패턴 활용
- **통합 테스트**: `@SpringBootTest` 어노테이션 활용

### 보안 고려사항
- **API 키 관리**: application.yml에서 환경변수로 분리 검토
- **외부 호출 제한**: WebClient timeout 및 retry 설정
- **로깅**: 민감 정보 로깅 방지

## 📝 학습 포인트

### Java vs Kotlin 비교 학습
1. **DTO 구현**: Java의 Lombok vs Kotlin의 data class
2. **널 안전성**: Java의 Optional vs Kotlin의 nullable types
3. **함수형 프로그래밍**: Stream API vs Kotlin sequences
4. **의존성 주입**: 생성자 주입 패턴 비교

### Spring WebFlux 학습
- **Mono/Flux**: 리액티브 스트림 이해
- **WebClient**: 비동기 HTTP 클라이언트 활용
- **Error Handling**: 리액티브 에러 처리 패턴

### 외부 API 연동 패턴
- **API 키 관리**: 설정 분리 및 보안
- **요청/응답 매핑**: DTO 변환 패턴
- **에러 처리**: 외부 API 실패 대응 전략

## 🔍 코드 품질 관리

### 정적 분석 도구 (향후 추가 검토)
- **SpotBugs**: 버그 패턴 감지
- **Checkstyle**: 코딩 컨벤션 검사
- **JaCoCo**: 테스트 커버리지 측정

### 문서화 개선
- **JavaDoc/KDoc**: API 문서 자동 생성
- **Architecture Decision Records**: 설계 결정 기록
- **API 사용 예시**: Swagger 예시 확장

이 프로젝트는 실무에서 자주 사용되는 외부 API 연동 패턴을 학습하고, Java와 Kotlin의 특성을 비교 학습할 수 있는 좋은 예제입니다.