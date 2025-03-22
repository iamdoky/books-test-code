package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AladinBookServiceImpl implements AladinBookService{

    @Value("${books.aladin.api.TTBKey}")
    private String ttbKey;

    private final WebClient aladinWebClient;

    public Mono<AladinBookResponse> search(AladinBookRequest request) {

        return aladinWebClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", ttbKey)
                .queryParam("Query", request.query())
                .queryParam("QueryType", request.queryType())
                .queryParam("MaxResults", request.maxResults())
                .queryParam("start", request.start())
                .queryParam("SearchTarget", request.searchTarget())
                .queryParam("Sort", request.sort())
                .queryParam("output", request.output())
                .queryParam("Version", request.version())
                .build())
            .retrieve()
            .bodyToMono(AladinBookResponse.class);
    }
}
