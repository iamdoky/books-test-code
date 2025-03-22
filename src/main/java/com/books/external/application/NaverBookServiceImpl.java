package com.books.external.application;

import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NaverBookServiceImpl implements NaverBookService {

    @Value("${books.naver.api.client-id}")
    private String clientId;

    @Value("${books.naver.api.client-secret}")
    private String clientSecret;

    private final WebClient naverWebClient;

    public Mono<NaverBookResponse> search(NaverSearchRequest request) {

        return naverWebClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/search/book.json")
                .queryParam("query", request.keyword())
                .queryParam("display", request.display())
                .queryParam("start", request.display())
                .build())
            .header("X-Naver-Client-Id", clientId)
            .header("X-Naver-Client-Secret", clientSecret)
            .retrieve()
            .bodyToMono(NaverBookResponse.class);
    }
}
