# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Spring Boot 3.4.4** application that integrates multiple external book search APIs (Aladin, Kakao, Naver) with dual-language implementation in **Java 21** and **Kotlin 1.9.22**. The project demonstrates identical functionality implemented in both languages for learning purposes.

## Development Commands

### Build & Run
```bash
# Run application
./gradlew bootRun

# Build project
./gradlew build

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.books.external.application.AladinBookServiceTest"

# Clean build
./gradlew clean build
```

### Database Access
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## Architecture Overview

### Dual Language Implementation
- **Java implementation**: `src/main/java/com/books/external/` - Complete external API integration (Aladin, Kakao, Naver)
- **Kotlin implementation**: `src/main/kotlin/com/books/` - Kakao API integration + internal book management

### Key Architectural Patterns

#### Facade Pattern
- **ExternalBooksFacade** (Java): Unifies three external book search services
- **KotlinBooksFacade** (Kotlin): Wraps Kakao book search service

#### Service Layer Structure
Each external API follows the pattern:
- `Service` interface
- `ServiceImpl` implementation
- Request/Response DTOs in dedicated packages

#### Package Organization
```
external/
├── api/                          # REST controllers & DTOs
│   ├── payload/request/          # API request models
│   └── payload/response/         # API response models
└── application/                  # Business logic services
```

### External API Integration
- **Aladin Books API**: TTB Key authentication
- **Kakao Search API**: KakaoAK token authentication  
- **Naver Search API**: Client ID/Secret authentication
- **WebFlux**: Asynchronous API calls using Mono/Flux

## Development Guidelines

### Language-Specific Implementation
- When modifying Java components, check for corresponding Kotlin implementations
- When adding new external API integrations, consider implementing in both languages
- Java code uses Lombok for boilerplate reduction
- Kotlin code leverages language features (data classes, null safety)

### API Key Management
- All API keys are configured in `application.yml`
- Never commit real API keys to version control
- Use environment variables for production deployments

### Testing Strategy
- Practice code examples in `src/test/java/com/books/java/practice/`
- Kotlin practice code in `src/test/java/com/books/kotlin/practice/`
- Service tests demonstrate WebClient mocking patterns

### WebClient Configuration
- Centralized in `WebClientConfig.java`
- Configured for external API timeouts and retry logic
- Uses reactive programming patterns (Mono/Flux)

## Common Development Tasks

### Adding New External Book API
1. Create request/response DTOs in appropriate package structure
2. Implement Service interface and ServiceImpl
3. Add configuration properties to `application.yml`
4. Update Facade class to integrate new service
5. Add corresponding controller endpoints
6. Consider implementing Kotlin version for learning comparison

### Running Individual Tests
```bash
# Java practice tests
./gradlew test --tests "com.books.java.practice.*"

# Kotlin practice tests  
./gradlew test --tests "com.books.kotlin.practice.*"

# External service tests
./gradlew test --tests "com.books.external.application.*"
```

### Debugging WebClient Issues
- Enable debug logging: `logging.level.org.springframework.web.reactive=DEBUG`
- Check H2 console for data persistence
- Verify API keys in application.yml
- Use Swagger UI for manual API testing