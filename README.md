# 📚 Books API - 도서 검색 통합 시스템

Spring Boot를 활용한 다중 외부 API 연동 도서 검색 시스템과 Java/Kotlin 학습 프로젝트

## 🎯 프로젝트 개요

이 프로젝트는 **알라딘**, **카카오**, **네이버** 도서 검색 API를 통합하여 제공하는 RESTful 서비스입니다. 
동일한 기능을 Java와 Kotlin으로 각각 구현하여 두 언어의 특성과 차이점을 학습할 수 있도록 구성되었습니다.

### 주요 기능
- 🔍 **다중 도서 검색 API 통합** (알라딘, 카카오, 네이버)
- 🎨 **Java/Kotlin 병행 구현** (동일 기능의 서로 다른 언어 구현체)
- 📖 **학습용 연습 코드** 포함
- 🌐 **Swagger UI** 를 통한 API 문서화
- ⚡ **WebFlux 기반 비동기 처리**

## 🛠 기술 스택

### Core Framework
- **Spring Boot** 3.4.4
- **Java** 21
- **Kotlin** 1.9.22
- **Gradle** 8.13

### Dependencies
- **Spring Data JPA** - 데이터 영속성
- **Spring WebFlux** - 비동기 웹 클라이언트
- **H2 Database** - 인메모리 데이터베이스
- **Swagger/OpenAPI** - API 문서화
- **Lombok** - Java 보일러플레이트 제거
- **JUnit 5** - 테스트 프레임워크

## 🏗 프로젝트 구조

```
src/
├── main/
│   ├── java/com/books/                 # Java 구현체
│   │   ├── config/                     # 설정 클래스
│   │   └── external/                   # 외부 API 연동
│   │       ├── api/                    # REST 컨트롤러
│   │       └── application/            # 비즈니스 로직
│   ├── kotlin/com/books/               # Kotlin 구현체
│   │   ├── book/                       # 도서 관련 기능
│   │   └── external/                   # 외부 API 연동 (Kotlin)
│   └── resources/
│       └── application.yml             # 애플리케이션 설정
└── test/
    └── java/com/books/
        ├── java/practice/              # Java 연습 코드
        └── kotlin/practice/            # Kotlin 연습 코드
```

### 주요 패키지 설명

- **`external/`**: 외부 도서 검색 API (알라딘, 카카오, 네이버) 연동 로직
- **`book/`**: 내부 도서 관리 기능 (Kotlin 구현)
- **`config/`**: WebClient 등 공통 설정
- **`practice/`**: Java/Kotlin 학습용 연습 코드 및 테스트

## 🚀 실행 방법

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd books
```

### 2. 애플리케이션 실행
```bash
# Gradle Wrapper 사용
./gradlew bootRun

# 또는 IDE에서 BooksApplication.main() 실행
```

### 3. 빌드
```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test
```

### 4. H2 데이터베이스 콘솔 접근
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (빈 문자열)

### 5. Swagger UI 접근
- URL: http://localhost:8080/swagger-ui.html
- API 문서화 및 테스트 가능
