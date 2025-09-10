package com.books.external;

import com.books.external.api.payload.request.aladin.KotlinAladinBookRequest;
import com.books.external.api.payload.request.naver.KotlinNaverSearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Kotlin Migration 기본 테스트")
public class KotlinMigrationTest {

    @Test
    @DisplayName("Kotlin Aladin Request 생성 테스트")
    void createKotlinAladinRequest() {
        // Given & When
        KotlinAladinBookRequest request = new KotlinAladinBookRequest(
                "클린코드", 
                "Keyword", 
                "10", 
                "1", 
                "Book", 
                "PublishTime", 
                "JS", 
                "20131101"
        );

        // Then
        assertThat(request.getQuery()).isEqualTo("클린코드");
        assertThat(request.getQueryType()).isEqualTo("Keyword");
        assertThat(request.getMaxResults()).isEqualTo("10");
    }

    @Test
    @DisplayName("Kotlin Naver Request 생성 테스트")
    void createKotlinNaverRequest() {
        // Given & When
        KotlinNaverSearchRequest request = new KotlinNaverSearchRequest("자바", 10, 1);

        // Then
        assertThat(request.getKeyword()).isEqualTo("자바");
        assertThat(request.getDisplay()).isEqualTo(10);
        assertThat(request.getStart()).isEqualTo(1);
    }

    @Test
    @DisplayName("Kotlin Naver Request 검증 테스트")
    void validateKotlinNaverRequest() {
        // Given & When & Then
        try {
            new KotlinNaverSearchRequest("", 10, 1);
            assert false : "빈 키워드는 예외를 발생시켜야 함";
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("검색 키워드는 필수입니다");
        }

        try {
            new KotlinNaverSearchRequest("테스트", 101, 1);
            assert false : "display 범위 초과는 예외를 발생시켜야 함";
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("display는 1-100 범위여야 합니다");
        }
    }
}