package com.books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "naverWebClient")
    public WebClient naverWebClient() {

        return WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .build();
    }

    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient() {

        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .build();
    }

    @Bean(name = "aladinWebClient")
    public WebClient aladinWebClient() {

        return WebClient.builder()
                .baseUrl("http://www.aladin.co.kr")
                .build();
    }
}