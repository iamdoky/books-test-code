package com.books.external.api;

import com.books.external.api.payload.request.aladin.AladinBookRequest;
import com.books.external.api.payload.request.kakao.KakaoSearchRequest;
import com.books.external.api.payload.request.naver.NaverSearchRequest;
import com.books.external.api.payload.response.aladin.AladinBookResponse;
import com.books.external.api.payload.response.kakao.KakaoBookResponse;
import com.books.external.api.payload.response.naver.NaverBookResponse;
import com.books.external.application.ExternalBooksFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalController.class)
class ExternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private ExternalBooksFacade externalBooksFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void search_알라딘_API_성공적으로_호출() throws Exception {
        // Given
        AladinBookRequest request = new AladinBookRequest(
                "Spring Boot",
                "Title",
                "10",
                "1",
                "Book",
                "SalesPoint",
                "js",
                "20131101"
        );

        AladinBookResponse response = new AladinBookResponse(
                "20131101",
                "http://image.aladin.co.kr/img/header/2003/aladin_logo_new.gif",
                "알라딘 검색결과 - Spring Boot",
                "http://www.aladin.co.kr/search/wsearchresult.aspx",
                "Wed, 06 Nov 2024 17:34:14 GMT",
                145,
                1,
                "Spring Boot",
                0,
                "전체",
                java.util.List.of()
        );

        when(externalBooksFacade.search(any(AladinBookRequest.class)))
                .thenReturn(Mono.just(response));

        // When & Then
        mockMvc.perform(post("/api/external/aladin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void search_카카오_API_성공적으로_호출() throws Exception {
        // Given
        KakaoSearchRequest request = new KakaoSearchRequest("Spring Boot", "title");

        KakaoBookResponse response = new KakaoBookResponse(
                java.util.List.of(),
                new com.books.external.api.payload.response.kakao.KakaoMeta(false, 50, 100)
        );

        when(externalBooksFacade.search(any(KakaoSearchRequest.class)))
                .thenReturn(Mono.just(response));

        // When & Then
        mockMvc.perform(post("/api/external/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void search_네이버_API_성공적으로_호출() throws Exception {
        // Given
        NaverSearchRequest request = new NaverSearchRequest("Spring Boot", 10, 1);

        NaverBookResponse response = new NaverBookResponse(
                "Wed, 06 Nov 2024 17:34:14 +0900",
                100,
                1,
                10,
                java.util.List.of()
        );

        when(externalBooksFacade.search(any(NaverSearchRequest.class)))
                .thenReturn(Mono.just(response));

        // When & Then
        mockMvc.perform(post("/api/external/naver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}