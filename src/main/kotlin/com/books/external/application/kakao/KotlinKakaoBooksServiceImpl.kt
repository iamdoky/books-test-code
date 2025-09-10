package com.books.external.application.kakao

import com.books.external.api.payload.kakao.request.KotlinKakaoSearchRequest
import com.books.external.api.payload.kakao.response.KotlinKakaoSearchResponse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class KotlinKakaoBooksServiceImpl(private val kakaoWebClient: WebClient) : KotlinKakaoBooksService {

    @Value("\${books.kakao.api.kakaoAK}")
    private lateinit var kakaoAK: String

    override suspend fun search(request: KotlinKakaoSearchRequest): KotlinKakaoSearchResponse {
        return kakaoWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/v3/search/book")
                    .queryParam("query", request.query)
                    .queryParam("target", request.target)
                    .build()
            }
            .header("Authorization", "KakaoAK $kakaoAK")
            .retrieve()
            .bodyToMono(KotlinKakaoSearchResponse::class.java)
            .awaitSingle()
    }

    override fun searchMono(request: KotlinKakaoSearchRequest): Mono<KotlinKakaoSearchResponse> {
        return mono { search(request) }
    }
}