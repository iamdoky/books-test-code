# 📚 Java → Kotlin 마이그레이션 가이드

## 🎯 마이그레이션 개요

현재 doky-book 프로젝트는 **Java로 구현된 3개 외부 API**(알라딘, 카카오, 네이버)와 **Kotlin으로 부분 구현된 카카오 API**가 혼재되어 있습니다. 이 가이드는 **Java 코드를 Kotlin으로 완전 마이그레이션**하기 위한 단계별 방법을 제시합니다.

### 현재 상태 분석
```
src/main/java/com/books/external/
├── api/ExternalController.java          ✅ 마이그레이션 대상
├── application/
│   ├── ExternalBooksFacade.java         ✅ 마이그레이션 대상  
│   ├── AladinBookServiceImpl.java       ✅ 마이그레이션 대상
│   ├── KakaoBookServiceImpl.java        ✅ 마이그레이션 대상
│   └── NaverBookServiceImpl.java        ✅ 마이그레이션 대상
└── payload/
    ├── request/ (Record 클래스들)        ✅ 마이그레이션 대상
    └── response/ (Record 클래스들)       ✅ 마이그레이션 대상

src/main/kotlin/com/books/external/
└── application/kakao/                   ⚡ 참고용 (부분 구현됨)
```

## 🗺️ 마이그레이션 로드맵

### Phase 1: DTO 클래스 변환 (Record → Data Class)
1. **Request DTO** 변환
2. **Response DTO** 변환  
3. **테스트 및 검증**

### Phase 2: Service 클래스 변환
1. **AladinBookService** 변환
2. **NaverBookService** 변환
3. **KakaoBookService** 개선 (기존 Kotlin 버전 활용)

### Phase 3: Controller & Facade 변환
1. **ExternalController** 변환
2. **ExternalBooksFacade** 변환
3. **통합 테스트**

### Phase 4: 최적화 & 정리
1. **Coroutines 적용** (선택사항)
2. **Java 코드 제거**
3. **문서 업데이트**

## 🔄 단계별 변환 방법

### 1. DTO 변환: Java Record → Kotlin Data Class

#### ✅ Before (Java Record)
```java
public record AladinBookRequest(
    @Schema(name = "query", description = "검색어", defaultValue = "어린왕자")
    String query,
    
    @Schema(name = "queryType", description = "검색어 종류", defaultValue = "Keyword")
    String queryType,
    
    @Schema(name = "maxResults", description = "검색결과 개수", defaultValue = "10")
    String maxResults
) {}
```

#### ✨ After (Kotlin Data Class)
```kotlin
import io.swagger.v3.oas.annotations.media.Schema

data class KotlinAladinBookRequest(
    @field:Schema(name = "query", description = "검색어", defaultValue = "어린왕자")
    val query: String,
    
    @field:Schema(name = "queryType", description = "검색어 종류", defaultValue = "Keyword")  
    val queryType: String,
    
    @field:Schema(name = "maxResults", description = "검색결과 개수", defaultValue = "10")
    val maxResults: String
)
```

#### 🔑 변환 포인트
- `record` → `data class`
- `@Schema` → `@field:Schema` (Kotlin에서 field 타겟 지정)
- `String` → `val query: String` (불변성 강조)

### 2. Service 클래스 변환

#### ✅ Before (Java Service)
```java
@Service
@RequiredArgsConstructor
public class AladinBookServiceImpl implements AladinBookService {

    @Value("${books.aladin.api.TTBKey}")
    private String ttbKey;

    private final WebClient aladinWebClient;

    public Mono<AladinBookResponse> search(AladinBookRequest request) {
        return aladinWebClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", ttbKey)
                .queryParam("Query", request.query())
                .build())
            .retrieve()
            .bodyToMono(AladinBookResponse.class);
    }
}
```

#### ✨ After (Kotlin Service)
```kotlin
@Service
class KotlinAladinBookService(
    private val aladinWebClient: WebClient
) : AladinBookService {

    @Value("\${books.aladin.api.TTBKey}")
    private lateinit var ttbKey: String

    override fun search(request: KotlinAladinBookRequest): Mono<KotlinAladinBookResponse> {
        return aladinWebClient.post()
            .uri { uriBuilder ->
                uriBuilder.path("/ttb/api/ItemSearch.aspx")
                    .queryParam("ttbkey", ttbKey)
                    .queryParam("Query", request.query)
                    .build()
            }
            .retrieve()
            .bodyToMono(KotlinAladinBookResponse::class.java)
    }
}
```

#### 🔑 변환 포인트
- `@RequiredArgsConstructor` → **Primary Constructor** 활용
- `private final` → `private val` (불변성)
- `@Value` + `lateinit var` (지연 초기화)
- `request.query()` → `request.query` (프로퍼티 접근)
- `AladinBookResponse.class` → `KotlinAladinBookResponse::class.java`

### 3. Controller 변환

#### ✅ Before (Java Controller)
```java
@RestController
@RequestMapping("/api/external")
@Slf4j
public class ExternalController {

    private final ExternalBooksFacade booksFacade;

    public ExternalController(ExternalBooksFacade booksFacade) {
        this.booksFacade = booksFacade;
    }

    @PostMapping(value = "/aladin")
    public ResponseEntity<Mono<AladinBookResponse>> search(
        @RequestBody AladinBookRequest request) {
        
        return ResponseEntity.ok(booksFacade.search(request));
    }
}
```

#### ✨ After (Kotlin Controller)
```kotlin
@RestController
@RequestMapping("/api/external")
@Slf4j
class KotlinExternalController(
    private val booksFacade: KotlinExternalBooksFacade
) {

    @PostMapping("/aladin")
    fun searchAladin(
        @RequestBody request: KotlinAladinBookRequest
    ): ResponseEntity<Mono<KotlinAladinBookResponse>> {
        return ResponseEntity.ok(booksFacade.search(request))
    }

    @PostMapping("/naver")
    fun searchNaver(
        @RequestBody request: KotlinNaverSearchRequest
    ): ResponseEntity<Mono<KotlinNaverBookResponse>> {
        return ResponseEntity.ok(booksFacade.search(request))
    }
}
```

#### 🔑 변환 포인트
- **생성자 주입** → Primary Constructor
- `public` 키워드 제거 (Kotlin 기본값)
- **메소드 오버로딩** → **명시적 메소드명** (searchAladin, searchNaver)
- `fun` 키워드로 함수 정의

### 4. Facade 클래스 변환

#### ✅ Before (Java Facade)
```java
@Service
@Slf4j
public class ExternalBooksFacade {
    
    private final AladinBookService aladinBookService;
    private final KakaoBookService kakaoBookService;
    private final NaverBookService naverBookService;

    // 생성자...

    public Mono<AladinBookResponse> search(AladinBookRequest request) {
        return aladinBookService.search(request);
    }

    public Mono<KakaoBookResponse> search(KakaoSearchRequest request) {
        return kakaoBookService.search(request);
    }
}
```

#### ✨ After (Kotlin Facade)
```kotlin
@Service
@Slf4j
class KotlinExternalBooksFacade(
    private val aladinBookService: KotlinAladinBookService,
    private val kakaoBookService: KotlinKakaoBooksService,
    private val naverBookService: KotlinNaverBookService
) {

    fun search(request: KotlinAladinBookRequest): Mono<KotlinAladinBookResponse> {
        return aladinBookService.search(request)
    }

    fun search(request: KotlinKakaoSearchRequest): Mono<KotlinKakaoSearchResponse> {
        return kakaoBookService.search(request)
    }

    fun search(request: KotlinNaverSearchRequest): Mono<KotlinNaverBookResponse> {
        return naverBookService.search(request)
    }
}
```

## 🚀 고급 최적화 옵션

### Coroutines 적용 (선택사항)

현재 **WebFlux (Mono/Flux)**를 사용하지만, Kotlin에서는 **Coroutines**가 더 자연스럽습니다.

#### Before (WebFlux)
```kotlin
fun search(request: KotlinAladinBookRequest): Mono<KotlinAladinBookResponse>
```

#### After (Coroutines)
```kotlin
suspend fun search(request: KotlinAladinBookRequest): KotlinAladinBookResponse {
    return aladinWebClient.post()
        .uri { /* ... */ }
        .retrieve()
        .awaitBody<KotlinAladinBookResponse>()
}
```

**장점**: 더 직관적인 비동기 코드, 예외 처리 개선
**단점**: Spring WebFlux와의 호환성 고려 필요

## 📋 마이그레이션 체크리스트

### Phase 1: DTO 변환
- [ ] `AladinBookRequest` → `KotlinAladinBookRequest`
- [ ] `AladinBookResponse` → `KotlinAladinBookResponse`
- [ ] `NaverSearchRequest` → `KotlinNaverSearchRequest`
- [ ] `NaverBookResponse` → `KotlinNaverBookResponse`
- [ ] `AladinSearchResponse` → `KotlinAladinSearchResponse`
- [ ] `SeriesInfo`, `SubInfo` → Kotlin data classes

### Phase 2: Service 변환
- [ ] `AladinBookService` 인터페이스 → Kotlin
- [ ] `AladinBookServiceImpl` → `KotlinAladinBookService`
- [ ] `NaverBookService` 인터페이스 → Kotlin
- [ ] `NaverBookServiceImpl` → `KotlinNaverBookService`
- [ ] 기존 `KotlinKakaoBooksService` 개선

### Phase 3: Controller & Facade
- [ ] `ExternalController` → `KotlinExternalController`
- [ ] `ExternalBooksFacade` → `KotlinExternalBooksFacade`

### Phase 4: 정리 작업
- [ ] Java 코드 제거
- [ ] Import 정리
- [ ] 테스트 코드 업데이트
- [ ] README.md 업데이트

## ⚠️ 주의사항 & Best Practices

### 1. Null Safety
```kotlin
// ❌ 피하기
var name: String? = null

// ✅ 권장
lateinit var name: String  // @Value 같은 지연 초기화용
val name: String = "default"  // 기본값 제공
```

### 2. Collection 불변성
```kotlin
// ❌ 피하기
var list: MutableList<String> = mutableListOf()

// ✅ 권장  
val list: List<String> = listOf()
```

### 3. Extension Functions 활용
```kotlin
// WebClient 확장 함수 예시
fun WebClient.RequestHeadersUriSpec<*>.withKakaoAuth(token: String) =
    this.header("Authorization", "KakaoAK $token")
```

### 4. Data Class 최적화
```kotlin
// ✅ 기본값 활용
data class SearchRequest(
    val query: String,
    val maxResults: Int = 10,
    val start: Int = 1
)
```

## 🧪 테스트 전략

### 1. 단계별 테스트
```kotlin
@Test
fun `알라딘 API 호출 테스트`() {
    // Given
    val request = KotlinAladinBookRequest(
        query = "어린왕자",
        queryType = "Keyword",
        maxResults = "10"
    )
    
    // When
    val result = aladinService.search(request)
    
    // Then
    assertThat(result).isNotNull
}
```

### 2. 호환성 테스트
- Java 구현체와 Kotlin 구현체 결과 비교
- API 응답 동일성 검증
- 성능 비교 테스트

## 📚 추가 학습 자료

### Kotlin 고급 기능
- **Sealed Classes**: 제한된 타입 계층 구조
- **Inline Classes**: 타입 안전성 + 성능
- **Delegation Pattern**: `by` 키워드 활용

### Spring + Kotlin
- **Configuration Properties**: `@ConfigurationProperties` Kotlin 활용
- **JPA Entity**: Kotlin data class 주의사항
- **Testing**: MockK vs Mockito

이 가이드를 통해 체계적으로 Java 코드를 Kotlin으로 마이그레이션하여 더 간결하고 안전한 코드베이스를 구축할 수 있습니다.