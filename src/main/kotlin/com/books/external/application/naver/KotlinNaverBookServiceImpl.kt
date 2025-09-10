package com.books.external.application.naver

import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest
import com.books.external.api.payload.response.naver.KotlinNaverBookResponse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class KotlinNaverBookServiceImpl(
    private val naverWebClient: WebClient
) : KotlinNaverBookService {

    @Value("\${books.naver.api.client-id}")
    private lateinit var clientId: String

    @Value("\${books.naver.api.client-secret}")
    private lateinit var clientSecret: String

    override suspend fun search(request: KotlinNaverSearchRequest): KotlinNaverBookResponse {
        return naverWebClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/v1/search/book.json")
                    .queryParam("query", request.keyword)
                    .queryParam("display", request.display)
                    .queryParam("start", request.start) // 버그 수정: display -> start
                    .build()
            }
            .header("X-Naver-Client-Id", clientId)
            .header("X-Naver-Client-Secret", clientSecret)
            .retrieve()
            .bodyToMono(KotlinNaverBookResponse::class.java)
            .awaitSingle()
    }

    override fun searchMono(request: KotlinNaverSearchRequest): Mono<KotlinNaverBookResponse> {
        return mono { search(request) }
    }
}