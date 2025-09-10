package com.books.external.api

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import com.books.external.application.KotlinUnifiedBooksFacade
import com.books.external.application.SearchStatistics
import com.books.external.application.UnifiedSearchResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/external/kotlin/advanced")
@Tag(name = "Kotlin 고급 외부 도서 호출", description = "Kotlin 고급 기능이 포함된 외부 도서 검색 API")
class KotlinAdvancedController(
    private val kotlinUnifiedBooksFacade: KotlinUnifiedBooksFacade
) {

    // ==================== Java Controller와 동일한 구조 ====================
    
    @PostMapping("/aladin")
    @Operation(summary = "알라딘 도서 검색", description = "알라딘 API를 통한 도서 검색 (Kotlin 구현)")
    fun searchAladin(
        @RequestBody request: KotlinAladinBookRequest
    ): ResponseEntity<Mono<KotlinAladinBookResponse>> {
        return ResponseEntity.ok(kotlinUnifiedBooksFacade.searchAladinMono(request))
    }

    @PostMapping("/kakao")
    @Operation(summary = "카카오 도서 검색", description = "카카오 API를 통한 도서 검색 (Kotlin 구현)")
    fun searchKakao(
        @RequestBody request: KotlinKakaoSearchRequest
    ): ResponseEntity<Mono<KotlinKakaoSearchResponse>> {
        return ResponseEntity.ok(kotlinUnifiedBooksFacade.searchKakaoMono(request))
    }

    @PostMapping("/naver")
    @Operation(summary = "네이버 도서 검색", description = "네이버 API를 통한 도서 검색 (Kotlin 구현)")
    fun searchNaver(
        @RequestBody request: KotlinNaverSearchRequest
    ): ResponseEntity<Mono<KotlinNaverBookResponse>> {
        return ResponseEntity.ok(kotlinUnifiedBooksFacade.searchNaverMono(request))
    }
    
    // ==================== Kotlin 향상된 기능들 ====================
    
    @GetMapping("/search/unified")
    @Operation(summary = "통합 검색 (Suspend)", description = "Kotlin suspend 함수로 모든 API를 병렬 호출")
    suspend fun unifiedSearch(
        @Parameter(description = "검색 키워드", example = "클린코드")
        @RequestParam keyword: String
    ): UnifiedSearchResult {
        return kotlinUnifiedBooksFacade.searchAll(keyword)
    }

    @GetMapping("/search/multiple")
    @Operation(summary = "선택적 다중 검색", description = "원하는 API들만 선택하여 병렬 검색")
    suspend fun searchMultiple(
        @Parameter(description = "검색 키워드", example = "스프링부트")
        @RequestParam keyword: String,
        
        @Parameter(description = "알라딘 포함 여부", example = "true")
        @RequestParam(defaultValue = "true") includeAladin: Boolean,
        
        @Parameter(description = "카카오 포함 여부", example = "true")
        @RequestParam(defaultValue = "true") includeKakao: Boolean,
        
        @Parameter(description = "네이버 포함 여부", example = "true")
        @RequestParam(defaultValue = "true") includeNaver: Boolean
    ): UnifiedSearchResult {
        return kotlinUnifiedBooksFacade.searchMultiple(keyword, includeAladin, includeKakao, includeNaver)
    }

    @GetMapping("/search/statistics")
    @Operation(summary = "검색 통계", description = "키워드에 대한 검색 결과 통계")
    suspend fun getSearchStatistics(
        @Parameter(description = "검색 키워드", example = "자바")
        @RequestParam keyword: String
    ): SearchStatistics {
        return kotlinUnifiedBooksFacade.getSearchStatistics(keyword)
    }

    @GetMapping("/health")
    @Operation(summary = "API 상태 체크", description = "모든 외부 API의 연결 상태를 병렬로 체크")
    suspend fun healthCheck(): Map<String, Any> {
        return try {
            val testResult = kotlinUnifiedBooksFacade.searchMultiple(
                keyword = "health-check",
                includeAladin = true,
                includeKakao = true,
                includeNaver = true
            )
            
            mapOf(
                "status" to "UP",
                "timestamp" to System.currentTimeMillis(),
                "services" to mapOf(
                    "aladin" to (if (testResult.aladinResult != null) "UP" else "DOWN"),
                    "kakao" to (if (testResult.kakaoResult != null) "UP" else "DOWN"),
                    "naver" to (if (testResult.naverResult != null) "UP" else "DOWN")
                ),
                "successfulApis" to testResult.getSuccessfulApiCount(),
                "totalApis" to 3,
                "searchKeyword" to "health-check"
            )
        } catch (e: Exception) {
            mapOf(
                "status" to "DOWN", 
                "timestamp" to System.currentTimeMillis(),
                "error" to (e.message ?: "Unknown error"),
                "services" to mapOf(
                    "aladin" to "UNKNOWN",
                    "kakao" to "UNKNOWN", 
                    "naver" to "UNKNOWN"
                )
            )
        }
    }
}