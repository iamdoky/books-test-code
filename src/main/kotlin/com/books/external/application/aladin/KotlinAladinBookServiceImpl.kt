package com.books.external.application.aladin

import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest
import com.books.external.api.payload.response.aladin.KotlinAladinBookResponse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class KotlinAladinBookServiceImpl(
    private val aladinWebClient: WebClient
) : KotlinAladinBookService {

    @Value("\${books.aladin.api.TTBKey}")
    private lateinit var ttbKey: String

    override suspend fun search(request: KotlinAladinBookRequest): KotlinAladinBookResponse {
        return aladinWebClient.post()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/ttb/api/ItemSearch.aspx")
                    .queryParam("ttbkey", ttbKey)
                    .queryParam("Query", request.query)
                    .queryParam("QueryType", request.queryType)
                    .queryParam("MaxResults", request.maxResults)
                    .queryParam("start", request.start)
                    .queryParam("SearchTarget", request.searchTarget)
                    .queryParam("Sort", request.sort)
                    .queryParam("output", request.output)
                    .queryParam("Version", request.version)
                    .build()
            }
            .retrieve()
            .bodyToMono(KotlinAladinBookResponse::class.java)
            .awaitSingle()
    }

    override fun searchMono(request: KotlinAladinBookRequest): Mono<KotlinAladinBookResponse> {
        return mono { search(request) }
    }
}