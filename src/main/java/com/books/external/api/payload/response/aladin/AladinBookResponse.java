package com.books.external.api.payload.response.aladin;


import java.util.List;

public record AladinBookResponse(

        String version,
        String logo,
        String title,
        String link,
        String pubDate,
        long totalResults,
        long startIndex,
        String query,
        long searchCategoryId,
        String searchCategoryName,
        List<AladinSearchResponse> item) {
}
