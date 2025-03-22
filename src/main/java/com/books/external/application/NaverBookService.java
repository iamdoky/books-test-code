package com.books.external.application;


import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import reactor.core.publisher.Mono;

public interface NaverBookService {

    Mono<NaverBookResponse> search(NaverSearchRequest request);
}
