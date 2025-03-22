package com.books.external.application;

import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import reactor.core.publisher.Mono;

public interface KakaoBookService {

    Mono<KakaoBookResponse> search(KakaoSearchRequest request);
}
