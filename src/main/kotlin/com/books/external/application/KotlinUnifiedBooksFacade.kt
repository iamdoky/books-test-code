package com.books.external.application

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import com.books.external.application.aladin.KotlinAladinBookService
import com.books.external.application.kakao.KotlinKakaoBooksService
import com.books.external.application.naver.KotlinNaverBookService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class KotlinUnifiedBooksFacade(
    private val aladinBookService: KotlinAladinBookService,
    private val kakaoBookService: KotlinKakaoBooksService,
    private val naverBookService: KotlinNaverBookService
) {

    // 개별 API 검색
    suspend fun searchAladin(request: KotlinAladinBookRequest): KotlinAladinBookResponse {
        return aladinBookService.search(request)
    }

    suspend fun searchKakao(request: KotlinKakaoSearchRequest): KotlinKakaoSearchResponse {
        return kakaoBookService.search(request)
    }

    suspend fun searchNaver(request: KotlinNaverSearchRequest): KotlinNaverBookResponse {
        return naverBookService.search(request)
    }

    // 통합 검색 (모든 API 동시 호출)
    suspend fun searchAll(keyword: String): UnifiedSearchResult = coroutineScope {
        
        val aladinRequest = KotlinAladinBookRequest(query = keyword)
        val kakaoRequest = KotlinKakaoSearchRequest(query = keyword, target = "title")
        val naverRequest = KotlinNaverSearchRequest(keyword = keyword)

        // 비동기 병렬 호출
        val aladinDeferred = async { 
            try { 
                aladinBookService.search(aladinRequest) 
            } catch (e: Exception) { 
                null 
            } 
        }
        
        val kakaoDeferred = async { 
            try { 
                kakaoBookService.search(kakaoRequest) 
            } catch (e: Exception) { 
                null 
            } 
        }
        
        val naverDeferred = async { 
            try { 
                naverBookService.search(naverRequest) 
            } catch (e: Exception) { 
                null 
            } 
        }

        UnifiedSearchResult(
            keyword = keyword,
            aladinResult = aladinDeferred.await(),
            kakaoResult = kakaoDeferred.await(),
            naverResult = naverDeferred.await(),
            searchTimestamp = System.currentTimeMillis()
        )
    }

    // 특정 API들만 검색 (선택적 병렬 호출)
    suspend fun searchMultiple(
        keyword: String,
        includeAladin: Boolean = true,
        includeKakao: Boolean = true,
        includeNaver: Boolean = true
    ): UnifiedSearchResult = coroutineScope {

        val aladinResult = if (includeAladin) {
            async { 
                try { 
                    aladinBookService.search(KotlinAladinBookRequest(query = keyword)) 
                } catch (e: Exception) { 
                    null 
                } 
            }
        } else null

        val kakaoResult = if (includeKakao) {
            async { 
                try { 
                    kakaoBookService.search(KotlinKakaoSearchRequest(query = keyword, target = "title")) 
                } catch (e: Exception) { 
                    null 
                } 
            }
        } else null

        val naverResult = if (includeNaver) {
            async { 
                try { 
                    naverBookService.search(KotlinNaverSearchRequest(keyword = keyword)) 
                } catch (e: Exception) { 
                    null 
                } 
            }
        } else null

        UnifiedSearchResult(
            keyword = keyword,
            aladinResult = aladinResult?.await(),
            kakaoResult = kakaoResult?.await(),
            naverResult = naverResult?.await(),
            searchTimestamp = System.currentTimeMillis()
        )
    }

    // Reactor 호환성을 위한 메서드들
    fun searchAladinMono(request: KotlinAladinBookRequest): Mono<KotlinAladinBookResponse> {
        return aladinBookService.searchMono(request)
    }

    fun searchKakaoMono(request: KotlinKakaoSearchRequest): Mono<KotlinKakaoSearchResponse> {
        return kakaoBookService.searchMono(request)
    }

    fun searchNaverMono(request: KotlinNaverSearchRequest): Mono<KotlinNaverBookResponse> {
        return naverBookService.searchMono(request)
    }

    // 검색 결과 통계
    suspend fun getSearchStatistics(keyword: String): SearchStatistics {
        val result = searchAll(keyword)
        
        return SearchStatistics(
            totalResults = listOfNotNull(
                result.aladinResult?.totalResults,
                result.kakaoResult?.meta?.total_count?.toLong(),
                result.naverResult?.total?.toLong()
            ).sum(),
            
            successfulApis = listOfNotNull(
                result.aladinResult?.let { "Aladin" },
                result.kakaoResult?.let { "Kakao" },
                result.naverResult?.let { "Naver" }
            ).size,
            
            failedApis = 3 - listOfNotNull(
                result.aladinResult,
                result.kakaoResult,
                result.naverResult
            ).size,
            
            searchKeyword = keyword
        )
    }
}

// 통합 검색 결과 데이터 클래스
data class UnifiedSearchResult(
    val keyword: String,
    val aladinResult: KotlinAladinBookResponse?,
    val kakaoResult: KotlinKakaoSearchResponse?,
    val naverResult: KotlinNaverBookResponse?,
    val searchTimestamp: Long
) {
    fun hasAnyResults(): Boolean = aladinResult != null || kakaoResult != null || naverResult != null
    
    fun getSuccessfulApiCount(): Int = listOfNotNull(aladinResult, kakaoResult, naverResult).size
    
    fun getTotalBookCount(): Long {
        return listOfNotNull(
            aladinResult?.totalResults,
            kakaoResult?.meta?.total_count?.toLong(),
            naverResult?.total?.toLong()
        ).sum()
    }
}

// 검색 통계 데이터 클래스
data class SearchStatistics(
    val totalResults: Long,
    val successfulApis: Int,
    val failedApis: Int,
    val searchKeyword: String
) {
    val successRate: Double = if (successfulApis + failedApis > 0) {
        (successfulApis.toDouble() / (successfulApis + failedApis)) * 100
    } else 0.0
}