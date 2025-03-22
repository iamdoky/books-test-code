package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import reactor.core.publisher.Mono;

public interface AladinBookService {

    Mono<AladinBookResponse> search(AladinBookRequest request);
}
