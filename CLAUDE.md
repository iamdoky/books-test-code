# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 리포지토리에서 코드 작업을 할 때 참고할 가이드를 제공합니다.

## 프로젝트 개요

이 프로젝트는 **Spring Boot 3.4.4** 애플리케이션으로, 여러 외부 도서 검색 API(알라딘, 카카오, 네이버)를 통합하며 **Java 21**과 **Kotlin 1.9.22**로 이중 언어 구현되어 있습니다. 학습 목적으로 동일한 기능을 두 언어로 각각 구현하여 비교할 수 있도록 구성되었습니다.

## 개발 명령어

### 환경 설정
```bash
# Java 21이 사용 가능한지 확인 (필수)
export JAVA_HOME=/path/to/java21
# 또는 SDKMAN 사용: sdk use java 21.0.3-oracle
```

### 빌드 & 실행
```bash
# Java 애플리케이션 실행
./gradlew bootRun
# 대안: IDE에서 BooksApplication.main() 실행

# Kotlin 애플리케이션 실행 (별도 진입점)
JAVA_HOME=/Users/iamdoky/Library/Java/JavaVirtualMachines/corretto-21.0.3/Contents/Home ./gradlew bootRun --args='--spring.main.class=com.books.BooksKotlinApplication'

# 프로젝트 빌드
JAVA_HOME=/Users/iamdoky/Library/Java/JavaVirtualMachines/corretto-21.0.3/Contents/Home ./gradlew build

# 모든 테스트 실행
JAVA_HOME=/Users/iamdoky/Library/Java/JavaVirtualMachines/corretto-21.0.3/Contents/Home ./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.books.external.application.AladinBookServiceTest"

# 클린 빌드
./gradlew clean build
```

### 데이터베이스 접근
- **H2 콘솔**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (빈 문자열)

### API 문서
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## 아키텍처 개요

### 이중 언어 구현
- **Java 구현체**: `src/main/java/com/books/external/` - 완전한 외부 API 통합 (알라딘, 카카오, 네이버)
- **Kotlin 구현체**: `src/main/kotlin/com/books/` - 완전한 외부 API 통합 (알라딘, 카카오, 네이버) + 내부 도서 관리
- **메인 애플리케이션 클래스들**:
  - `BooksApplication.java` - Java 진입점
  - `BooksKotlinApplication.kt` - 코루틴 지원이 강화된 Kotlin 진입점

### 주요 아키텍처 패턴

#### Facade 패턴
- **ExternalBooksFacade** (Java): 세 개의 외부 도서 검색 서비스를 순차 처리로 통합
- **KotlinBooksFacade** (Kotlin): 카카오 도서 검색 서비스 래퍼
- **KotlinUnifiedBooksFacade** (Kotlin): 코루틴을 사용한 병렬 API 호출을 지원하는 고급 Facade

#### 서비스 레이어 구조
각 외부 API는 다음 패턴을 따릅니다:
- `Service` 인터페이스
- `ServiceImpl` 구현체
- 전용 패키지의 Request/Response DTO들

#### 패키지 구조
```
external/
├── api/                          # REST 컨트롤러 & DTO
│   ├── payload/request/          # API 요청 모델
│   └── payload/response/         # API 응답 모델
└── application/                  # 비즈니스 로직 서비스
```

### 외부 API 통합
- **알라딘 도서 API**: TTB Key 인증
- **카카오 검색 API**: KakaoAK 토큰 인증
- **네이버 검색 API**: Client ID/Secret 인증
- **WebFlux**: Mono/Flux를 사용한 비동기 API 호출

## 개발 가이드라인

### 언어별 구현 특성
- Java 컴포넌트를 수정할 때는 해당하는 Kotlin 구현체가 있는지 확인
- 새로운 외부 API 통합을 추가할 때는 두 언어로 모두 구현하는 것을 고려
- Java 코드는 Lombok으로 보일러플레이트 코드 줄임
- Kotlin 코드는 언어 특성 활용 (데이터 클래스, null 안전성)

### API 키 관리
- 모든 API 키는 `application.yml`에 설정됨 (현재 키들은 데모/예제용)
- 운영환경용: 환경변수나 외부 설정 사용
- 키 설정 형식:
  ```yaml
  books:
    aladin.api.TTBKey: "${ALADIN_API_KEY:demo_key}"
    kakao.api.kakaoAK: "${KAKAO_API_KEY:demo_key}"
    naver.api.client-id: "${NAVER_CLIENT_ID:demo_key}"
    naver.api.client-secret: "${NAVER_CLIENT_SECRET:demo_key}"
  ```

### 테스트 전략
- **학습/연습 테스트**:
  - Java 연습: `src/test/java/com/books/java/practice/` (17+ 문제)
  - Kotlin 연습: `src/test/kotlin/com/books/kotlin/practice/` (20+ 문제)
- **서비스 테스트**: WebClient 모킹을 활용한 외부 API 통합 테스트
- **마이그레이션 테스트**: `KotlinMigrationTest.java`로 Java→Kotlin 데이터 클래스 변환 검증
- **통합 테스트**: 전체 애플리케이션 컨텍스트 테스트

### WebClient 설정
- `WebClientConfig.java`에서 중앙화
- 외부 API 타임아웃과 재시도 로직 설정
- 리액티브 프로그래밍 패턴 사용 (Mono/Flux)

## 공통 개발 작업

### 새로운 외부 도서 API 추가하기
1. 적절한 패키지 구조에 request/response DTO 생성
2. Service 인터페이스와 ServiceImpl 구현
3. `application.yml`에 설정 속성 추가
4. 새 서비스를 통합하도록 Facade 클래스 업데이트
5. 해당 컨트롤러 엔드포인트 추가
6. 학습 비교를 위한 Kotlin 버전 구현 고려

### 개별 테스트 실행
```bash
# Java 연습 테스트 (17+ 학습 문제)
./gradlew test --tests "com.books.java.practice.*"

# Kotlin 연습 테스트 (20+ 학습 문제)
./gradlew test --tests "com.books.kotlin.practice.*"

# 외부 서비스 테스트 (API 통합)
./gradlew test --tests "com.books.external.application.*"

# 마이그레이션 검증 테스트
./gradlew test --tests "*Migration*"

# Java 21로 모든 테스트
JAVA_HOME=/Users/iamdoky/Library/Java/JavaVirtualMachines/corretto-21.0.3/Contents/Home ./gradlew test

# 애플리케이션 테스트만
./gradlew test --tests "*BooksApplicationTests*"
```

### WebClient 이슈 디버깅
- 디버그 로깅 활성화: `logging.level.org.springframework.web.reactive=DEBUG`
- 데이터 지속성을 위한 H2 콘솔 확인
- application.yml의 API 키 검증
- 수동 API 테스트를 위한 Swagger UI 사용

## 마이그레이션 컨텍스트

이 프로젝트는 **Java → Kotlin 마이그레이션 패턴**을 나란히 비교할 수 있는 구현체로 보여줍니다:

- **학습 목적**: 동일한 기능에 대한 Java vs Kotlin 접근 방식 비교
- **마이그레이션 문서**: 상세한 마이그레이션 보고서는 `KOTLIN_MIGRATION_SUMMARY.md` 참조
- **아키텍처 패턴**:
  - Java: Mono/Flux 리액티브 스트림을 사용한 전통적인 Spring Boot
  - Kotlin: 코루틴, suspend 함수, 병렬 처리로 향상됨
- **성능 비교**: Kotlin 구현체는 Java의 순차 처리 대비 병렬 API 호출 포함