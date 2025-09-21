# 🚀 프로젝트 개선사항 구현 가이드

**작성일**: 2025-09-15
**목적**: 분석 결과를 바탕으로 한 구체적인 개선사항 구현 방법 제시

---

## 📋 개선사항 우선순위별 구현 가이드

### 🔴 Critical Priority (즉시 조치 필요)

#### 1. API 키 환경변수 이관

**현재 문제**:
```yaml
# application.yml - 보안 위험
books:
  aladin:
    api:
      TTBKey: "ttbkdh6102309002"  # 하드코딩됨!
```

**개선 방법**:

1. **application.yml 수정**:
```yaml
books:
  aladin:
    api:
      TTBKey: "${ALADIN_API_KEY:default_dev_key}"
  kakao:
    api:
      kakaoAK: "${KAKAO_API_KEY:default_dev_key}"
  naver:
    api:
      client-id: "${NAVER_CLIENT_ID:default_dev_id}"
      client-secret: "${NAVER_CLIENT_SECRET:default_dev_secret}"
```

2. **프로파일별 설정 파일 생성**:

`application-dev.yml`:
```yaml
books:
  aladin:
    api:
      TTBKey: "dev_aladin_key"
  kakao:
    api:
      kakaoAK: "dev_kakao_key"
  naver:
    api:
      client-id: "dev_naver_id"
      client-secret: "dev_naver_secret"
```

`application-prod.yml`:
```yaml
books:
  aladin:
    api:
      TTBKey: "${ALADIN_API_KEY}"
  kakao:
    api:
      kakaoAK: "${KAKAO_API_KEY}"
  naver:
    api:
      client-id: "${NAVER_CLIENT_ID}"
      client-secret: "${NAVER_CLIENT_SECRET}"
```

3. **환경변수 설정**:
```bash
# 개발 환경
export ALADIN_API_KEY="your_aladin_key"
export KAKAO_API_KEY="your_kakao_key"
export NAVER_CLIENT_ID="your_naver_id"
export NAVER_CLIENT_SECRET="your_naver_secret"
```

**검증 방법**: `SecurityImprovementTest.java` 실행

---

#### 2. WebClient Connection Pool 설정

**현재 문제**: 기본 설정으로 성능 최적화 부족

**개선 방법**:

`WebClientConfig.java` 수정:
```java
@Configuration
public class ImprovedWebClientConfig {

    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("kakao-pool")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .wiretap(true); // 디버깅용

        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "naverWebClient")
    public WebClient naverWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("naver-pool")
                .maxConnections(30)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5))
                .option(ChannelOption.SO_KEEPALIVE, true);

        return WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "aladinWebClient")
    public WebClient aladinWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("aladin-pool")
                .maxConnections(30)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // 알라딘은 좀 더 여유
                .responseTimeout(Duration.ofSeconds(8))
                .option(ChannelOption.SO_KEEPALIVE, true);

        return WebClient.builder()
                .baseUrl("http://www.aladin.co.kr")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
```

**검증 방법**: `PerformanceImprovementTest.java` 실행

---

#### 3. 구조화된 로깅 추가

**현재 문제**: 로깅이 거의 없어 운영 모니터링 어려움

**개선 방법**:

1. **로깅 라이브러리 추가** (`build.gradle`):
```gradle
dependencies {
    implementation 'net.logstash.logback:logstash-logback-encoder:7.4'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

2. **Service 클래스에 로깅 추가**:

`KakaoBookServiceImpl.java` 개선:
```java
@Service
@Slf4j
public class ImprovedKakaoBookServiceImpl implements KakaoBookService {

    @Value("${books.kakao.api.kakaoAK}")
    private String kakaoAK;

    private final WebClient kakaoWebClient;

    @Override
    public Mono<KakaoBookResponse> search(KakaoSearchRequest request) {
        String requestId = UUID.randomUUID().toString();

        log.info("카카오 도서 검색 시작 - requestId: {}, query: {}, target: {}, page: {}, size: {}",
                requestId, request.getQuery(), request.getTarget(), request.getPage(), request.getSize());

        long startTime = System.currentTimeMillis();

        return kakaoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v3/search/book")
                        .queryParam("query", request.getQuery())
                        .queryParam("target", request.getTarget())
                        .queryParam("page", request.getPage())
                        .queryParam("size", request.getSize())
                        .build())
                .header("Authorization", "KakaoAK " + kakaoAK)
                .retrieve()
                .bodyToMono(KakaoBookResponse.class)
                .doOnSuccess(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("카카오 도서 검색 성공 - requestId: {}, duration: {}ms, resultCount: {}",
                            requestId, duration, response.getDocuments().size());
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("카카오 도서 검색 실패 - requestId: {}, duration: {}ms, error: {}",
                            requestId, duration, error.getMessage(), error);
                })
                .onErrorResume(error -> {
                    log.warn("카카오 API 호출 실패 후 기본값 반환 - requestId: {}, error: {}",
                            requestId, error.getMessage());
                    return Mono.just(new KakaoBookResponse()); // 기본값 반환
                });
    }
}
```

3. **Kotlin 서비스에 로깅 추가**:

`KotlinKakaoBooksServiceImpl.kt` 개선:
```kotlin
@Service
class ImprovedKotlinKakaoBooksServiceImpl(
    @Qualifier("kakaoWebClient") private val kakaoWebClient: WebClient,
    @Value("\${books.kakao.api.kakaoAK}") private val kakaoAK: String
) : KotlinKakaoBooksService {

    private val logger = LoggerFactory.getLogger(ImprovedKotlinKakaoBooksServiceImpl::class.java)

    override suspend fun search(request: KotlinKakaoSearchRequest): KotlinKakaoSearchResponse {
        val requestId = UUID.randomUUID().toString()

        logger.info("코틀린 카카오 도서 검색 시작 - requestId: {}, query: {}",
                   requestId, request.query)

        val startTime = System.currentTimeMillis()

        return try {
            val response = kakaoWebClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("/v3/search/book")
                        .queryParam("query", request.query)
                        .queryParam("target", request.target)
                        .queryParam("page", request.page)
                        .queryParam("size", request.size)
                        .build()
                }
                .header("Authorization", "KakaoAK $kakaoAK")
                .retrieve()
                .awaitBody<KotlinKakaoSearchResponse>()

            val duration = System.currentTimeMillis() - startTime
            logger.info("코틀린 카카오 도서 검색 성공 - requestId: {}, duration: {}ms, resultCount: {}",
                       requestId, duration, response.documents.size)

            response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error("코틀린 카카오 도서 검색 실패 - requestId: {}, duration: {}ms, error: {}",
                        requestId, duration, e.message, e)
            throw e
        }
    }
}
```

4. **logback-spring.xml** 설정:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="!prod">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <stackTrace/>
                </providers>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="com.books" level="DEBUG"/>
    </springProfile>

    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/books-app.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/books-app.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>3GB</totalSizeCap>
            </rollingPolicy>
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <stackTrace/>
                </providers>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

**검증 방법**: `LoggingImprovementTest.java` 실행

---

### 🟡 High Priority (단기 계획 - 1개월)

#### 4. 입력 검증 강화

**build.gradle에 의존성 추가**:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

**Request DTO 검증 추가**:
```java
public class ValidatedKakaoSearchRequest {

    @NotBlank(message = "검색어는 필수입니다")
    @Size(min = 1, max = 100, message = "검색어는 1-100자 사이여야 합니다")
    private String query;

    @Pattern(regexp = "^(title|isbn|publisher|person)$",
             message = "target은 title, isbn, publisher, person 중 하나여야 합니다")
    private String target;

    @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
    @Max(value = 50, message = "페이지는 50 이하여야 합니다")
    private Integer page;

    @Min(value = 1, message = "사이즈는 1 이상이어야 합니다")
    @Max(value = 50, message = "사이즈는 50 이하여야 합니다")
    private Integer size;

    // getters, setters...
}
```

**Controller 수정**:
```java
@PostMapping(value = "/kakao")
public ResponseEntity<Mono<KakaoBookResponse>> search(
    @Valid @RequestBody ValidatedKakaoSearchRequest request) {
    // ...
}
```

#### 5. 커스텀 예외 클래스 구현

**예외 클래스 생성**:
```java
// 기본 예외
public class BooksApiException extends RuntimeException {
    private final String apiProvider;
    private final String errorCode;

    public BooksApiException(String apiProvider, String errorCode, String message) {
        super(message);
        this.apiProvider = apiProvider;
        this.errorCode = errorCode;
    }

    public BooksApiException(String apiProvider, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.apiProvider = apiProvider;
        this.errorCode = errorCode;
    }

    // getters...
}

// 특정 API 예외들
public class KakaoApiException extends BooksApiException {
    public KakaoApiException(String errorCode, String message) {
        super("KAKAO", errorCode, message);
    }
}

public class AladinApiException extends BooksApiException {
    public AladinApiException(String errorCode, String message) {
        super("ALADIN", errorCode, message);
    }
}

public class NaverApiException extends BooksApiException {
    public NaverApiException(String errorCode, String message) {
        super("NAVER", errorCode, message);
    }
}
```

#### 6. 캐싱 전략 도입

**build.gradle에 캐시 의존성 추가**:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'com.github.ben-manes.caffeine:caffeine'
```

**캐시 설정**:
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());
        return cacheManager;
    }
}
```

**Service에 캐시 적용**:
```java
@Cacheable(value = "kakaoSearch", key = "#request.query + '-' + #request.target + '-' + #request.page")
public Mono<KakaoBookResponse> search(KakaoSearchRequest request) {
    // 기존 로직
}
```

---

### 🟢 Medium Priority (중장기 계획 - 2-3개월)

#### 7. Circuit Breaker 패턴 적용

**build.gradle에 Resilience4j 추가**:
```gradle
implementation 'io.github.resilience4j:resilience4j-spring-boot3'
implementation 'io.github.resilience4j:resilience4j-reactor'
```

**Circuit Breaker 설정**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      kakaoApi:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
```

#### 8. 메트릭 수집 시스템

**Micrometer 메트릭 추가**:
```java
@Component
public class BooksMetrics {

    private final Counter kakaoApiCallCounter;
    private final Timer kakaoApiResponseTimer;

    public BooksMetrics(MeterRegistry meterRegistry) {
        this.kakaoApiCallCounter = Counter.builder("books.api.calls")
                .description("Number of API calls")
                .tag("provider", "kakao")
                .register(meterRegistry);

        this.kakaoApiResponseTimer = Timer.builder("books.api.response.time")
                .description("API response time")
                .tag("provider", "kakao")
                .register(meterRegistry);
    }

    public void incrementKakaoApiCall() {
        kakaoApiCallCounter.increment();
    }

    public Timer.Sample startKakaoApiTimer() {
        return Timer.start(kakaoApiResponseTimer);
    }
}
```

---

## 🧪 테스트 실행 방법

### 1. 보안 테스트
```bash
./gradlew test --tests "com.books.security.SecurityImprovementTest"
```

### 2. 성능 테스트
```bash
./gradlew test --tests "com.books.performance.PerformanceImprovementTest"
```

### 3. 로깅 테스트
```bash
./gradlew test --tests "com.books.logging.LoggingImprovementTest"
```

### 4. 전체 개선사항 테스트
```bash
./gradlew test --tests "com.books.security.*" --tests "com.books.performance.*" --tests "com.books.logging.*"
```

---

## 📊 개선 효과 측정

### 1. 보안 개선 지표
- [ ] API 키 하드코딩 제거
- [ ] 환경변수 기반 설정 적용
- [ ] 프로파일별 설정 분리

### 2. 성능 개선 지표
- [ ] Connection Pool 설정으로 동시 요청 처리 능력 향상
- [ ] 평균 응답 시간 단축
- [ ] 리소스 사용량 최적화

### 3. 운영성 개선 지표
- [ ] 구조화된 로깅으로 모니터링 가능
- [ ] 에러 추적 및 디버깅 용이성 향상
- [ ] 성능 메트릭 수집 가능

---

**작성자**: Claude Code Analysis
**업데이트**: 2025-09-15
**다음 검토**: 개선사항 적용 후