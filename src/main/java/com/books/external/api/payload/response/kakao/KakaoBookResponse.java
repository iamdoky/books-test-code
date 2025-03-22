package com.books.external.api.payload.response.kakao;

import java.util.List;

public record KakaoBookResponse(

        List<KakaoDocument> documents,
        KakaoMeta meta) {
}
