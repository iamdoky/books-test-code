package com.books.external.api;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import com.books.external.application.ExternalBooksFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "외부 도서 호출")
@RestController
@RequestMapping("/api/external")
@Slf4j
public class ExternalController {

    private final ExternalBooksFacade booksFacade;

    public ExternalController(ExternalBooksFacade booksFacade) {
        this.booksFacade = booksFacade;
    }

    @PostMapping(value = "/aladin")
    public ResponseEntity<Mono<AladinBookResponse>> search(
        @RequestBody AladinBookRequest request) {

        return ResponseEntity.ok(booksFacade.search(request));
    }

    @PostMapping(value = "/kakao")
    public ResponseEntity<Mono<KakaoBookResponse>> search(
        @RequestBody KakaoSearchRequest request) {

        return ResponseEntity.ok(booksFacade.search(request));
    }

    @PostMapping(value = "/naver")
    public ResponseEntity<Mono<NaverBookResponse>> search(
        @RequestBody NaverSearchRequest request) {

        return ResponseEntity.ok(booksFacade.search(request));
    }
}
