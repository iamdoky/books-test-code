package com.books.external.api.payload.response.kakao;

public record KakaoMeta(

        boolean is_end,
        int pageable_count,
        int total_count) {
}
