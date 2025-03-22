package com.books.external.application;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ExternalBooksFacade {

    private final AladinBookService aladinBookService;
    private final KakaoBookService kakaoBookService;
    private final NaverBookService naverBookService;

    public ExternalBooksFacade(
        AladinBookService aladinBookService,
        KakaoBookService kakaoBookService,
        NaverBookService naverBookService) {

        this.aladinBookService = aladinBookService;
        this.kakaoBookService = kakaoBookService;
        this.naverBookService = naverBookService;
    }

    public Mono<AladinBookResponse> search(AladinBookRequest request) {

        return aladinBookService.search(request);
    }

    public Mono<KakaoBookResponse> search(KakaoSearchRequest request) {

        return kakaoBookService.search(request);
    }

    public Mono<NaverBookResponse> search(NaverSearchRequest request) {

        return naverBookService.search(request);
    }
}
