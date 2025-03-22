package com.books.external.api.payload.response.kakao;

public record KakaoDocument(

        String[] authors,
        String contents,
        String datetime,
        String isbn,
        int price,
        String publisher,
        int salePrice,
        String status,
        String thumbnail,
        String title,
        String[] translators,
        String url) {
}
